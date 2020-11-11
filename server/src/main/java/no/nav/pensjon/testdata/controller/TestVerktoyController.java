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
import no.nav.tjeneste.domene.pensjon.behandleautomatiskomregning.v1.meldinger.AutomatiskOmregningAvYtelseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import javax.xml.datatype.DatatypeConfigurationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Consumer;

@RestController
@Api(tags = {"Testverktøy"})
@SwaggerDefinition(tags = {
        @Tag(name = "", description = "Endepunkter som inneholder støttefunksjonalitet for test")
})
@RequestMapping("/api")
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
    private Counter omregningCounter;

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

        omregningCounter = Counter
                .builder("pensjon.testdata.omregning.total")
                .description("Antall saker som er automatisk omregnet")
                .register(meterRegistry);
    }

    @PostMapping("/iverksett")
    @Transactional
    public ResponseEntity iverksett(@RequestBody IverksettVedtakRequest request) {
        try {
            mockService.iverksett(request.getVedtakId());
            iverksettVedtakCounter.increment();
        } catch (Exception e) {
            logger.error("Klarte ikke å iverksette " + e.getMessage(), e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, getStracktrace(e), e);
        }

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/attester")
    @Transactional
    public ResponseEntity attester(@RequestBody IverksettVedtakRequest request) {
        try {
            mockService.attester(request.getVedtakId());
            attestedVedtakCounter.increment();
        } catch (Exception e) {
            logger.error("Could not attest " + e.getMessage(), e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, getStracktrace(e), e);
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/flytte-sak")
    @Transactional
    public ResponseEntity flyttSak(@RequestBody FlyttSakRequest request) {
        try {
            mockService.flyttEnhet(request.getSakId(), request.getNyEnhet());
            flyttSakCounter.increment();
        } catch (Exception e) {
            logger.error("Could not move case" + e.getMessage(), e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, getStracktrace(e), e);
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/person/{fnr}")
    @Transactional
    public ResponseEntity<HttpStatus> opprettPerson(@PathVariable String fnr) {
        opprettPersonIgnorerDuplikater(mockService::opprettPenPerson, fnr);
        opprettPersonIgnorerDuplikater(mockService::opprettPoppPerson, fnr);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    private void opprettPersonIgnorerDuplikater(Consumer<String> consumer, String fnr) {
        try {
            consumer.accept(fnr);
        }
        catch (DuplicateKeyException e){
            logger.warn("personen med fnr: " + fnr + " finnes i DB fra før");
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, getStracktrace(e), e);
        }
    }

    @GetMapping("/enheter")
    @Transactional
    public ResponseEntity<String> hentEnheter() {
        return ResponseEntity.ok(norgConsumerBean.hentEnheter());
    }

    @PostMapping("/omregning")
    @Transactional
    public ResponseEntity<String> automatiskOmregning(@RequestBody AutomatiskOmregningRequest request) throws DatatypeConfigurationException, JsonProcessingException {
        omregningCounter.increment();
        AutomatiskOmregningAvYtelseResponse response = automatiskOmregning.automatiskOmregning(request);
        StringBuilder sb = createOmregningTextResponse(response);

        if (response.getFeilmelding() != null || response.getFunksjonellFeilmelding() != null) {
            return new ResponseEntity<>(sb.toString(), HttpStatus.EXPECTATION_FAILED);
        } else {
            return ResponseEntity.ok(sb.toString());
        }
    }

    private StringBuilder createOmregningTextResponse(AutomatiskOmregningAvYtelseResponse response) {
        StringBuilder sb = new StringBuilder();
        sb.append("Behandling ferdig, status: " +  response.getStatus());
        if (response.getFunksjonellFeilmelding() != null) {
            sb.append(" Funksjonell feilmelding: " + response.getFunksjonellFeilmelding());
        }
        if (response.getFeilmelding() != null) {
            sb.append(" Teknisk feilmelding: " + response.getFeilmelding());
        }
        return sb;
    }

    private String getStracktrace(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
