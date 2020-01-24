package no.nav.pensjon.testdata.controller;

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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

@RestController
public class MockController {

    private final Logger logger = LoggerFactory.getLogger(MockController.class);

    @Autowired
    MockService mockService;

    @RequestMapping(method = RequestMethod.POST, path = "/iverksett")
    public ResponseEntity iverksett(@RequestBody IverksettVedtakRequest request ) throws IOException, SQLException {
        mockService.iverksett(request.getVedtakId());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/attester")
    public ResponseEntity attester(@RequestBody IverksettVedtakRequest request ) throws IOException, SQLException {
        mockService.attester(request.getVedtakId());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/flytte-sak")
    public ResponseEntity flyttSak(@RequestBody FlyttSakRequest request ) throws IOException, SQLException {
        mockService.flyttEnhet(request.getSakId(), request.getNyEnhet());
        return ResponseEntity.ok(HttpStatus.OK);
    }
    @PostMapping(path = "/person/{fnr}")
    @Transactional
    public ResponseEntity<HttpStatus> opprettPerson(@PathVariable String fnr) {
        try {
            mockService.opprettPerson(fnr);
        } catch (Exception e) {
            e.printStackTrace();
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, sw.toString(), e);
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
