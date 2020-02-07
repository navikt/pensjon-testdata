package no.nav.pensjon.testdata.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    /*
     * Operasjonen dekker et ønske fra Dolly om  å vite hvilke miljøer Pensjon er tilgjengelig.
     */
    @RequestMapping(method = RequestMethod.GET, path = "/miljo")
    public ResponseEntity<List<String>> opprettPerson() {
        List<String> miljo = new ArrayList<>();
        miljo.add("q0");
        miljo.add("q1");
        miljo.add("q2");
        miljo.add("q3");
        miljo.add("q4");
        miljo.add("q5");
        miljo.add("q6");
        miljo.add("q8");
        miljo.add("t0");
        miljo.add("t1");
        miljo.add("t2");
        miljo.add("t3");
        miljo.add("t4");
        miljo.add("t5");
        miljo.add("t6");
        miljo.add("t8");
        miljo.add("u12");
        miljo.add("u15");
        miljo.add("u18");
        miljo.add("u8");
        return ResponseEntity.ok(miljo);
    }

    private class OpprettPersonRequest {
        @NotBlank(message = "Paakrevd fnr")
        String fnr;
        Date fodselsDato;
        Date dodsDato;
        Date utvandingsDato;
        String bostedsland;
        List<String> miljo;

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

        public List<String> getMiljo() {
            return miljo;
        }

        public void setMiljo(List<String> miljo) {
            this.miljo = miljo;
        }
    }

}
