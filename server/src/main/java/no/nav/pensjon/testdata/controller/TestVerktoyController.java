package no.nav.pensjon.testdata.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import no.nav.pensjon.testdata.controller.support.FlyttSakRequest;
import no.nav.pensjon.testdata.controller.support.IverksettVedtakRequest;
import no.nav.pensjon.testdata.service.MockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.PrintWriter;
import java.io.StringWriter;

@RestController
@Api(tags = {"Testverktøy"})
@SwaggerDefinition(tags = {
        @Tag(name = "", description = "Endepunkter som inneholder støttefunksjonalitet for test")
})
public class TestVerktoyController {

    private final Logger logger = LoggerFactory.getLogger(TestVerktoyController.class);

    @Autowired
    MockService mockService;

    @RequestMapping(method = RequestMethod.POST, path = "/iverksett")
    public ResponseEntity iverksett(@RequestBody IverksettVedtakRequest request) {
        try {
            mockService.iverksett(request.getVedtakId());
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, getStracktrace(e), e);
        }

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/attester")
    public ResponseEntity attester(@RequestBody IverksettVedtakRequest request) {
        try {
            mockService.attester(request.getVedtakId());
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, getStracktrace(e), e);
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/flytte-sak")
    public ResponseEntity flyttSak(@RequestBody FlyttSakRequest request) {
        try {
            mockService.flyttEnhet(request.getSakId(), request.getNyEnhet());
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, getStracktrace(e), e);
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping(path = "/person/{fnr}")
    @Transactional
    public ResponseEntity<HttpStatus> opprettPerson(@PathVariable String fnr) {
        try {
            mockService.opprettPerson(fnr);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, getStracktrace(e), e);
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    private String getStracktrace(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
