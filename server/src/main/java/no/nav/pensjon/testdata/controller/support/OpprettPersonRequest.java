package no.nav.pensjon.testdata.controller.support;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

public class OpprettPersonRequest {
    @NotEmpty(message = "Paakrevd fnr")
    private String fnr;
    @NotEmpty(message = "Paakrevd fodselsdato")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date fodselsDato;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date dodsDato;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date utvandringsDato;
    private String bostedsland;

    public OpprettPersonRequest() {
        super();
    }

    public OpprettPersonRequest(String fnr, Date fodselsDato, Date dodsDato, Date utvandringsDato, String bostedsland) {
        this.fnr = fnr;
        this.fodselsDato = fodselsDato;
        this.dodsDato = dodsDato;
        this.utvandringsDato = utvandringsDato;
        this.bostedsland = bostedsland;
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

    public Date getUtvandringsDato() {
        return utvandringsDato;
    }

    public void setUtvandringsDato(Date utvandringsDato) {
        this.utvandringsDato = utvandringsDato;
    }

    public String getBostedsland() {
        return bostedsland;
    }

    public void setBostedsland(String bostedsland) {
        this.bostedsland = bostedsland;
    }
}
