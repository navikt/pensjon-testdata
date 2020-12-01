package no.nav.pensjon.testdata.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.annotations.Api;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import no.nav.pensjon.testdata.consumer.grunnbelop.GrunnbelopConsumerBean;
import no.nav.pensjon.testdata.controller.support.*;
import no.nav.pensjon.testdata.repository.FileRepository;
import no.nav.pensjon.testdata.repository.OracleRepository;
import no.nav.pensjon.testdata.repository.ScenarioRepository;
import no.nav.pensjon.testdata.repository.support.TestScenarioUtil;
import no.nav.pensjon.testdata.repository.support.validators.AbstractScenarioValidator;
import no.nav.pensjon.testdata.repository.support.validators.ScenarioValidationException;
import no.nav.pensjon.testdata.service.TestdataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Api(tags = {"Testdata"})
@SwaggerDefinition(tags = {
        @Tag(name = "", description = "Endepunkter som gjennomfører behandling av testdata innenfor pensjonsområdet")
})
@RequestMapping("/api/testdata")
public class TestdataController {

    private static final Logger logger = LoggerFactory.getLogger(TestdataController.class);

    @Autowired
    OracleRepository oracleRepository;

    @Autowired
    TestdataService testdataService;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    ScenarioRepository scenarioRepository;

    @Autowired
    GrunnbelopConsumerBean grunnbelopConsumer;

    @Autowired
    private MeterRegistry meterRegistry;

    @PostMapping
    public ResponseEntity createTestdata(@RequestBody CreateTestdataRequest request) {

        Counter opprettTestdataTotal = Counter
                .builder("pensjon.testdata.opprett.scenario.total")
                .tags("scenario", request.getTestCaseId())
                .description("Opprettet testdata-scenario")
                .register(meterRegistry);

        try {
            testdataService.createTestcase(
                    request.getTestCaseId(),
                    request.getHandlebars());

            opprettTestdataTotal.increment();

        } catch (IOException e) {
            logger.error("Could not find requested testcase: " + request.getTestCaseId(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ScenarioValidationException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getTestcases() {
        List<GetTestcasesResponse.Testcase> testcases = scenarioRepository.getAllTestScenarios().stream()
                .map(s -> new GetTestcasesResponse.Testcase(
                        s.getScenarioId(),
                        s.getName(),
                        TestScenarioUtil.getAllePersoner(s).stream()
                        .map(p -> p.getKontrollers().stream()
                                .map(AbstractScenarioValidator::getDescription)
                                .collect(Collectors.joining(",")))
                        .filter(description  -> !description.isEmpty())
                        .collect(Collectors.toList()),
                        s.getFritekstbeskrivelse())
                )
                .collect(Collectors.toList());
        return ResponseEntity.ok(new GetTestcasesResponse(testcases));
    }

    @GetMapping("/handlebars/{testcase}")
    public ResponseEntity<List<Handlebar>> getTestcaseHandlebars(@PathVariable String testcase) {
        try {
            return ResponseEntity.ok(fileRepository.getTestcaseHandlebars(testcase));
        } catch (IOException e) {
            logger.error("Could not find requested testcase: " + testcase, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Could not create testcase", e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, getStracktrace(e), e);
        }

    }

    @PostMapping("/clear")
    public ResponseEntity delete(@RequestBody ClearTestdataRequest request) {
        try {
            oracleRepository.clearDatabase();
        } catch (Exception e) {
            logger.error("Clearing of database failed " + e.getMessage(), e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, getStracktrace(e), e);
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/clear/{fnr}")
    public ResponseEntity clearDataForPerson(@PathVariable String fnr) {
        Thread t = new Thread(() -> {
            try {
                oracleRepository.clearDatabaseForPerson(fnr);
            } catch (IOException e) {
                logger.error("Could not clear db for person " + e.getMessage(), e);
                throw new RuntimeException(e);
            }
        });
        t.start();
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/log")
    public ResponseEntity<List> fetchTestdata(@RequestBody FetchTestdataRequest request)
            throws IOException, SQLException {
        logger.info("Processing request from: " + request.getFom() + " to: " + request.getTom() + " with: " + request.getIdenter().toString());
        List<String> resultat = testdataService.fetchTestdataLog(request.getFom(), request.getTom(), request.getIdenter());
        logger.info("Processing complete");
        return ResponseEntity.ok(resultat);
    }

    private String getStracktrace(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
