package no.nav.pensjon.testdata.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import no.nav.pensjon.testdata.repository.support.validators.ScenarioValidationException;
import no.nav.pensjon.testdata.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@Api(tags = {"Validation"})
@SwaggerDefinition(tags = {@Tag(name = "", description = "Endepunkter for validering av brukerinput")})
@RequestMapping("/api")
public class ValidationController {

    @Autowired
    private ValidationService validationService;

    @PostMapping("/validation")
    public ResponseEntity<String> validateHandleBars(@RequestParam String handlebar, @RequestParam String value){
        try{
            validationService.validate(Collections.singletonMap(handlebar, value));
        }
        catch(ScenarioValidationException e){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(e.getMessage());
        }
        return ResponseEntity.ok("OK");
    }
}
