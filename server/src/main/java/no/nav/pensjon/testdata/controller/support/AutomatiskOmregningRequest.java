package no.nav.pensjon.testdata.controller.support;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public class AutomatiskOmregningRequest {

    private String fnr;
    private String sakId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate virkFom;

    public String getFnr() {
        return fnr;
    }

    public void setFnr(String fnr) {
        this.fnr = fnr;
    }

    public String getSakId() {
        return sakId;
    }

    public void setSakId(String sakId) {
        this.sakId = sakId;
    }

    public LocalDate getVirkFom() {
        return virkFom;
    }

    public void setVirkFom(LocalDate virkFom) {
        this.virkFom = virkFom;
    }
}
