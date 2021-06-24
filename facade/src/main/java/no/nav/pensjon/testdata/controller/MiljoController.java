package no.nav.pensjon.testdata.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import no.nav.pensjon.testdata.configuration.support.Environment;
import no.nav.pensjon.testdata.configuration.support.EnvironmentResolver;
import no.nav.pensjon.testdata.consumer.opptjening.TestdataConsumerBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@Api(tags = {"Miljo"})
@SwaggerDefinition(tags = {
        @Tag(name = "", description = "Endepunkter for håndtering av miljødistribusjon til pensjonsområdet")
})
@RequestMapping("/api/v1/miljo")
public class MiljoController {

    private static final Logger LOG = LoggerFactory.getLogger(MiljoController.class);

    @Autowired
    TestdataConsumerBean testdataConsumerBean;

    @GetMapping
    @ApiOperation(value = "Miljøer der endepunkt for samhandling for testdata inn mot Pensjonsområdet er tilgjengelig")
    public ResponseEntity<List<String>> opprettPerson() throws IOException {
        List<String> miljo = new ArrayList<>();

        EnvironmentResolver
                .getAvaiableEnvironments()
                .entrySet()
                .stream()
                .forEach(entry -> miljo.add(entry.getValue().getEnv()));

        return ResponseEntity.ok(miljo);
    }

    @GetMapping("/status")
    @ApiOperation(value = "DB-status per miljø")
    public List<String> dbStatus() {
        Collection<Environment> environments = EnvironmentResolver
                .getAvaiableEnvironments().values();
        List<String> status = new ArrayList<>();
        for (Environment env : environments){
            try {
                status.add(env.getEnv() + ": " + testdataConsumerBean.getStatus(env.getUrl()));
            }
            catch (ResourceAccessException e){
                LOG.error("Could not connect to env " + env.getUrl(), e);
                status.add(env.getEnv() + ": " + e.toString());
            }
        }
        return status;
    }
}
