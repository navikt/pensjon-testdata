package no.nav.pensjon.testdata.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.annotations.Api;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import no.nav.pensjon.testdata.consumer.norg.NorgConsumerBean;
import no.nav.pensjon.testdata.consumer.omregning.AutomatiskOmregning;
import no.nav.pensjon.testdata.controller.support.AutomatiskOmregningRequest;
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

import javax.annotation.PostConstruct;
import javax.xml.datatype.DatatypeConfigurationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;

@RestController
@Api(tags = {"Testverktøy"})
@SwaggerDefinition(tags = {
        @Tag(name = "", description = "Endepunkter som inneholder støttefunksjonalitet for test")
})
public class TestVerktoyController {

    private final Logger logger = LoggerFactory.getLogger(TestVerktoyController.class);

    @Autowired
    private MockService mockService;

    @Autowired
    private NorgConsumerBean norgConsumerBean;

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    private AutomatiskOmregning automatiskOmregning;

    private Counter iverksettVedtakCounter;
    private Counter attestedVedtakCounter;
    private Counter flyttSakCounter;

    @PostConstruct
    private void initCounters() {
        iverksettVedtakCounter = Counter
                .builder("pensjon.testdata.iverksett.vedtak.total")
                .description("Vedtak iverksatt")
                .register(meterRegistry);

        attestedVedtakCounter = Counter
                .builder("pensjon.testdata.attester.vedtak.total")
                .description("Vedtak attestert")
                .register(meterRegistry);

        flyttSakCounter = Counter
                .builder("pensjon.testdata.flytt.sak.total")
                .description("Sak flyttet fra en eierenhet til en annen")
                .register(meterRegistry);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/iverksett")
    public ResponseEntity iverksett(@RequestBody IverksettVedtakRequest request) {
        try {
            mockService.iverksett(request.getVedtakId());
            iverksettVedtakCounter.increment();
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
            attestedVedtakCounter.increment();
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
            flyttSakCounter.increment();
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

    @RequestMapping(method = RequestMethod.GET, path = "/enheter")
    public ResponseEntity<String> hentEnheter() {
        return ResponseEntity.ok(norgConsumerBean.hentEnheter());
    }

    @RequestMapping(method = RequestMethod.POST, path = "/omregning")
    public ResponseEntity<String> automatiskOmregning(@RequestBody AutomatiskOmregningRequest request) throws DatatypeConfigurationException, JsonProcessingException {
        return ResponseEntity.ok(automatiskOmregning.automatiskOmregning(request));
    }

    private String getStracktrace(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
