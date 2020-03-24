package no.nav.pensjon.testdata.controller.support.response;

import java.util.List;

public class Inntekter {
    private String miljo;
    private String fnr;
    private List<Inntekt> inntekter;

    public String getMiljo() {
        return miljo;
    }

    public void setMiljo(String miljo) {
        this.miljo = miljo;
    }

    public String getFnr() {
        return fnr;
    }

    public void setFnr(String fnr) {
        this.fnr = fnr;
    }

    public List<Inntekt> getInntekter() {
        return inntekter;
    }

    public void setInntekter(List<Inntekt> inninntekter) {
        inntekter = inninntekter;
    }
}
