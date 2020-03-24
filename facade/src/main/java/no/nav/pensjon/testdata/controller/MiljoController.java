package no.nav.pensjon.testdata.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import no.nav.pensjon.testdata.configuration.support.EnvironmentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
@Api(tags = {"Miljo"})
@SwaggerDefinition(tags = {
        @Tag(name = "", description = "Endepunkter for håndtering av miljødistribusjon til pensjonsområdet")
})
public class MiljoController {

    @Autowired
    private ResourceLoader resourceLoader;

    @RequestMapping(method = RequestMethod.GET, path = "/api/v1/miljo")
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
}
