package no.nav.pensjon.testdata.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pensjon.testdata.consumer.opptjening.OpptjeningConsumerBean;
import no.nav.pensjon.testdata.consumer.opptjening.support.Inntekt;
import no.nav.pensjon.testdata.consumer.opptjening.support.LagreInntektPoppRequest;
import no.nav.pensjon.testdata.controller.support.LagreInntektRequest;
import no.nav.pensjon.testdata.service.POPPDataExtractorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
public class OpptjeningController {

    @Autowired
    private OpptjeningConsumerBean opptjeningConsumerBean;

    @Autowired
    POPPDataExtractorService poppDataExtractorService;


    private ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/popp/{fnr}")
    public ResponseEntity<List<String>> fetchFromPopp(@PathVariable String fnr) throws IOException {
        List<String> data = poppDataExtractorService.extractDataFromPOPP(fnr);
        return ResponseEntity.ok(data);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/inntekt")
    public ResponseEntity lagreInntekt(@RequestBody LagreInntektRequest request) throws JsonProcessingException {
        for (int aar = request.getFomAar(); aar<= request.getTomAar(); aar++ ) {
            Inntekt inntekt = new Inntekt(request.getFnr(), aar, request.getBelop(), "INN_LON");
            LagreInntektPoppRequest poppRequest = new LagreInntektPoppRequest(inntekt);

            String body = objectMapper.writeValueAsString(poppRequest);

            opptjeningConsumerBean.lagreInntekt(body);
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

}
