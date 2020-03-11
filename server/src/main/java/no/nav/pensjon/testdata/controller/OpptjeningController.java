package no.nav.pensjon.testdata.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.annotations.Api;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import no.nav.pensjon.testdata.configuration.support.JdbcTemplateWrapper;
import no.nav.pensjon.testdata.consumer.grunnbelop.GrunnbelopConsumerBean;
import no.nav.pensjon.testdata.consumer.opptjening.OpptjeningConsumerBean;
import no.nav.pensjon.testdata.consumer.opptjening.support.Inntekt;
import no.nav.pensjon.testdata.consumer.opptjening.support.LagreInntektPoppRequest;
import no.nav.pensjon.testdata.controller.support.InntektPOPP;
import no.nav.pensjon.testdata.controller.support.LagreInntektRequest;
import no.nav.pensjon.testdata.repository.support.ComponentCode;
import no.nav.pensjon.testdata.service.POPPDataExtractorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Comparator;
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

    @Autowired
    private JdbcTemplateWrapper jdbcTemplateWrapper;

    private ObjectMapper objectMapper = new ObjectMapper();

    private static Integer GRUNNBELOP_2019 = 99858;

    private static Counter hentPoppDataCounter;

    private static Counter lagreInntektDataCounter;

    @Autowired
    private MeterRegistry meterRegistry;

    @PostConstruct
    private void initCounters() {
        hentPoppDataCounter = Counter
                .builder("pensjon.testdata.hent.fra.popp.total")
                .description("Data hentet fra popp")
                .register(meterRegistry);

        lagreInntektDataCounter = Counter
                .builder("pensjon.testdata.lagre.inntekt.popp.total")
                .description("Inntekt lagret mot POPP")
                .register(meterRegistry);
    }

    @GetMapping("/popp/{fnr}")
    public ResponseEntity<List<String>> fetchFromPopp(@PathVariable String fnr) throws IOException {
        List<String> data = poppDataExtractorService.extractDataFromPOPP(fnr);

        hentPoppDataCounter.increment();

        return ResponseEntity.ok(data);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/inntekt")
    public ResponseEntity lagreInntekt(@RequestBody LagreInntektRequest request) throws IOException {
        for (int aar = request.getFomAar(); aar <= request.getTomAar(); aar++) {
            Inntekt inntekt = fastsettInntekt(request, aar);

            LagreInntektPoppRequest poppRequest = new LagreInntektPoppRequest(inntekt);

            lagreInntekt(poppRequest);
        }

        lagreInntektDataCounter.increment();

        return ResponseEntity.ok(HttpStatus.OK);
    }

    private Inntekt fastsettInntekt(@RequestBody LagreInntektRequest request, int aar) throws IOException {
        if (!request.isRedusertMedGrunnbelop()) {
            return new Inntekt(request.getFnr(), aar, request.getBelop(), "INN_LON");
        } else {
            Map<Integer, Long> grunnbelop = grunnbelopConsumerBean.hentVeietGrunnbelop();

            float faktor = (float) grunnbelop.get(aar) / GRUNNBELOP_2019;
            int nyInntekt = Math.round(faktor * request.getBelop());

            logger.debug("Fastsetter inntekt for aar: "
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

    @RequestMapping(method = RequestMethod.GET, path = "/inntekt")
    public ResponseEntity<List<InntektPOPP>> hentInntekt(
            @RequestHeader("Nav-Call-Id") String callId,
            @RequestHeader("Nav-Consumer-Id") String consumerId,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam String fnr,
            @RequestParam(defaultValue = "q2") String miljo)  {

        String sql = "select i.inntekt_ar, i.belop " +
                "from popp.t_person p " +
                "inner join popp.t_inntekt i on i.person_id = p.person_id " +
                "where i.k_inntekt_status = 'G' " +
                "and i.k_inntekt_t = 'SUM_PI' " +
                "and p.fnr_fk = '" + fnr + "'";

        List<InntektPOPP> inntekter = jdbcTemplateWrapper.queryForList(ComponentCode.POPP, sql, (rs, rowNum) -> {
            InntektPOPP inntektPOPP = new InntektPOPP();
            inntektPOPP.setInntektAar(rs.getInt("inntekt_ar"));
            inntektPOPP.setBelop(rs.getLong("belop"));
            return inntektPOPP;
        });
        inntekter.sort(Comparator.comparing(InntektPOPP::getInntektAar).reversed());
        return ResponseEntity.ok(inntekter);
    }

}
