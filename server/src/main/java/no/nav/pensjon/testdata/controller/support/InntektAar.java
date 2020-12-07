package no.nav.pensjon.testdata.controller.support;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InntektAar {
    @JsonProperty
    private int aar;
    @JsonProperty
    private int inntekt;

    public InntektAar(int aar, int inntekt) {
        this.aar = aar;
        this.inntekt = inntekt;
    }

    public int getAar() {
        return aar;
    }

    public int getInntekt() {
        return inntekt;
    }
}
