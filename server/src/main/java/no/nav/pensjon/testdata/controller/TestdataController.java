package no.nav.pensjon.testdata.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.annotations.Api;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;

import no.nav.pensjon.testdata.consumer.grunnbelop.GrunnbelopConsumerBean;
import no.nav.pensjon.testdata.controller.support.ClearTestdataRequest;
import no.nav.pensjon.testdata.controller.support.CreateTestdataRequest;
import no.nav.pensjon.testdata.controller.support.FetchTestdataRequest;
import no.nav.pensjon.testdata.controller.support.GetTestcasesResponse;
import no.nav.pensjon.testdata.controller.support.Handlebar;
import no.nav.pensjon.testdata.repository.FileRepository;
import no.nav.pensjon.testdata.repository.OracleRepository;
import no.nav.pensjon.testdata.repository.support.validators.AbstractScenarioValidator;
import no.nav.pensjon.testdata.repository.support.validators.ScenarioValidationException;
import no.nav.pensjon.testdata.service.TestdataService;

@RestController
@Api(tags = {"Testdata"})
@SwaggerDefinition(tags = {
        @Tag(name = "", description = "Endepunkter som gjennomfører behandling av testdata innenfor pensjonsområdet")
})
public class TestdataController {

    private final Logger logger = LoggerFactory.getLogger(TestdataController.class);

    @Autowired
    OracleRepository oracleRepository;

    @Autowired
    TestdataService testdataService;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    GrunnbelopConsumerBean grunnbelopConsumer;

    @Autowired
    private MeterRegistry meterRegistry;

    @RequestMapping(method = RequestMethod.POST, path = "/testdata")
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
            logger.info("Could not find requested testcase: " + request.getTestCaseId(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ScenarioValidationException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/testdata")
    public ResponseEntity getTestcases() {
        try {
            List<GetTestcasesResponse.Testcase> testcases = fileRepository.getAllTestcases().stream()
                    .map(s -> new GetTestcasesResponse.Testcase(s.getName(), s.getAllePersoner().stream()
                            .map(p -> p.getKontrollers().stream()
                                    .map(AbstractScenarioValidator::getDescription)
                                    .collect(Collectors.joining(",")))
                            .filter(description  -> !description.isEmpty())
                            .collect(Collectors.toList()))
                    )
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new GetTestcasesResponse(testcases));
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, getStracktrace(e), e);
        }
    }

    @GetMapping("/testdata/handlebars/{testcase}")
    public ResponseEntity<List<Handlebar>> getTestcaseHandlebars(@PathVariable String testcase) {
        try {
            Set<String> result = fileRepository.getTestcaseHandlebars(testcase);
            return ResponseEntity.ok(result
                    .stream()
                    .map(Handlebar::new)
                    .collect(Collectors.toList()));
        } catch (IOException e) {
            logger.info("Could not find requested testcase: " + testcase, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.info("Could not create testcase", e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, getStracktrace(e), e);
        }

    }

    @RequestMapping(method = RequestMethod.POST, path = "/testdata/clear")
    public ResponseEntity delete(@RequestBody ClearTestdataRequest request) {
        try {
            oracleRepository.clearDatabase();
        } catch (Exception e) {
            logger.info("Clearing of database failed", e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, getStracktrace(e), e);
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/testdata/clear/{fnr}")
    public ResponseEntity clearDataForPerson(@PathVariable String fnr) {
        Thread t = new Thread(() -> {
            try {
                oracleRepository.clearDatabaseForPerson(fnr);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        t.start();
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/testdata/log")
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
