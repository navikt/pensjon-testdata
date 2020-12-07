package no.nav.pensjon.testdata.controller.support;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InntektAar {
    @JsonProperty
    private int aar;
    @JsonProperty
    private long inntekt;

    public InntektAar(int aar, long inntekt) {
        this.aar = aar;
        this.inntekt = inntekt;
    }

    public int getAar() {
        return aar;
    }

    public long getInntekt() {
        return inntekt;
    }
}
