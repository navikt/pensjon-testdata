package no.nav.pensjon.testdata.controller;

import no.nav.pensjon.testdata.consumer.grunnbelop.GrunnbelopConsumerBean;
import no.nav.pensjon.testdata.controller.support.ClearTestdataRequest;
import no.nav.pensjon.testdata.controller.support.CreateTestdataRequest;
import no.nav.pensjon.testdata.controller.support.GetTestcasesResponse;
import no.nav.pensjon.testdata.controller.support.Handlebar;
import no.nav.pensjon.testdata.repository.FileRepository;
import no.nav.pensjon.testdata.repository.OracleRepository;
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
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

    @RequestMapping(method = RequestMethod.POST, path = "/testdata")
    public ResponseEntity createTestdata(@RequestBody CreateTestdataRequest request) {
        try {
            testdataService.createTestcase(
                    request.getTestCaseId(),
                    request.getHandlebars());
        } catch (IOException e) {
            logger.info("Could not find requested testcase: " + request.getTestCaseId(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.info("Could not create testcase", e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, getStracktrace(e), e);
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/testdata")
    public ResponseEntity getTestcases() {
        List<String> testcases = null;
        try {
            testcases = fileRepository.getAllTestcases();
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, getStracktrace(e), e);
        }

        return ResponseEntity.ok(new GetTestcasesResponse(testcases));
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

    @GetMapping("/testdata/canclear/")
    public ResponseEntity<Boolean> canDbBeCleared() throws IOException {
        if (oracleRepository.canDatabaseBeCleared()) {
            return ResponseEntity.ok(Boolean.TRUE);
        } else {
            return ResponseEntity.ok(Boolean.FALSE);
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

    private String getStracktrace(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
