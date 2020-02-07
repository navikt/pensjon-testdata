package no.nav.pensjon.testdata.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@RestController
@Api(tags = {"Person"})
@SwaggerDefinition(tags = {
        @Tag(name = "", description = "Endepunkter for person")
})
public class PersonController {

    @RequestMapping(method = RequestMethod.POST, path = "/person")
    public ResponseEntity<HttpStatus> opprettPerson(@RequestHeader("Nav-Call-Id") String callId, @RequestHeader("Nav-Consumer-Id") String consumerId, @RequestBody OpprettPersonRequest body) {

        return ResponseEntity.ok(HttpStatus.OK);
    }

    private class OpprettPersonRequest {
        @NotBlank(message = "Paakrevd fnr")
        String fnr;
        Date fodselsDato;
        Date dodsDato;
        Date utvandingsDato;
        String bostedsland;

        public String getFnr() {
            return fnr;
        }

        public void setFnr(String fnr) {
            this.fnr = fnr;
        }

        public Date getFodselsDato() {
            return fodselsDato;
        }

        public void setFodselsDato(Date fodselsDato) {
            this.fodselsDato = fodselsDato;
        }

        public Date getDodsDato() {
            return dodsDato;
        }

        public void setDodsDato(Date dodsDato) {
            this.dodsDato = dodsDato;
        }

        public Date getUtvandingsDato() {
            return utvandingsDato;
        }

        public void setUtvandingsDato(Date utvandingsDato) {
            this.utvandingsDato = utvandingsDato;
        }

        public String getBostedsland() {
            return bostedsland;
        }

        public void setBostedsland(String bostedsland) {
            this.bostedsland = bostedsland;
        }

    }

}
