package no.nav.pensjon.testdata.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.prometheus.client.Counter;
import io.swagger.annotations.Api;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import no.nav.pensjon.testdata.consumer.grunnbelop.GrunnbelopConsumerBean;
import no.nav.pensjon.testdata.consumer.opptjening.OpptjeningConsumerBean;
import no.nav.pensjon.testdata.consumer.opptjening.support.Inntekt;
import no.nav.pensjon.testdata.consumer.opptjening.support.LagreInntektPoppRequest;
import no.nav.pensjon.testdata.controller.support.LagreInntektRequest;
import no.nav.pensjon.testdata.service.POPPDataExtractorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@Api(tags = {"Opptjening"})
@SwaggerDefinition(tags = {
        @Tag(name = "", description = "Endepunkter som gjennomfører behandling av opptjening")
})
public class OpptjeningController {

    Logger logger = LoggerFactory.getLogger(OpptjeningController.class);

    @Autowired
    private OpptjeningConsumerBean opptjeningConsumerBean;

    @Autowired
    private POPPDataExtractorService poppDataExtractorService;

    @Autowired
    private GrunnbelopConsumerBean grunnbelopConsumerBean;

    private ObjectMapper objectMapper = new ObjectMapper();

    private static Integer GRUNNBELOP_2019 = 99858;

    private Counter hentPoppDataCounter = Counter.build().name("pensjon_testdata_hent_fra_popp").help("Data hentet fra popp").register();

    @GetMapping("/popp/{fnr}")
    public ResponseEntity<List<String>> fetchFromPopp(@PathVariable String fnr) throws IOException {
        List<String> data = poppDataExtractorService.extractDataFromPOPP(fnr);

        hentPoppDataCounter.inc();

        return ResponseEntity.ok(data);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/inntekt")
    public ResponseEntity lagreInntekt(@RequestBody LagreInntektRequest request) throws IOException {
        logger.info("Lagrer inntekt: " + request.toString());
        for (int aar = request.getFomAar(); aar <= request.getTomAar(); aar++) {
            Inntekt inntekt = fastsettInntekt(request, aar);

            LagreInntektPoppRequest poppRequest = new LagreInntektPoppRequest(inntekt);

            lagreInntekt(poppRequest);
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    private Inntekt fastsettInntekt(@RequestBody LagreInntektRequest request, int aar) throws IOException {
        if (!request.isRedusertMedGrunnbelop()) {
            return new Inntekt(request.getFnr(), aar, request.getBelop(), "INN_LON");
        } else {
            Map<Integer, Long> grunnbelop = grunnbelopConsumerBean.hentVeietGrunnbelop();

            float faktor = (float) grunnbelop.get(aar) / GRUNNBELOP_2019;
            int nyInntekt = Math.round(faktor * request.getBelop());

            logger.info("Fastsetter inntekt for aar: "
                    + aar + " med grunnbelop: "
                    + grunnbelop.get(aar) + " Ny inntekt er: "
                    + nyInntekt
                    + " Formel= avrund(" + grunnbelop.get(aar) + "/" + GRUNNBELOP_2019 + ") * " + request.getBelop() + ")");

            return new Inntekt(request.getFnr(), aar, Long.valueOf(nyInntekt), "INN_LON");
        }
    }

    private void lagreInntekt(LagreInntektPoppRequest poppRequest) throws JsonProcessingException {
        String body = objectMapper.writeValueAsString(poppRequest);
        opptjeningConsumerBean.lagreInntekt(body);
    }

}
