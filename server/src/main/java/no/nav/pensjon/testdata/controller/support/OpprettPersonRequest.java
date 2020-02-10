package no.nav.pensjon.testdata.controller.support;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

public class OpprettPersonRequest {
    @NotBlank(message = "Paakrevd fnr")
    private String fnr;
    private Date fodselsDato;
    private Date dodsDato;
    private Date utvandingsDato;
    private String bostedsland;
    private List<String> miljo;

    public OpprettPersonRequest() {
        super();
    }

    public OpprettPersonRequest(String fnr, Date fodselsDato, Date dodsDato, Date utvandingsDato, String bostedsland, List<String> miljo) {
        this.fnr = fnr;
        this.fodselsDato = fodselsDato;
        this.dodsDato = dodsDato;
        this.utvandingsDato = utvandingsDato;
        this.bostedsland = bostedsland;
        this.miljo = miljo;
    }

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
