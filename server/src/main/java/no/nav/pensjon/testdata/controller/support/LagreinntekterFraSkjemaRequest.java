package no.nav.pensjon.testdata.controller.support;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class LagreinntekterFraSkjemaRequest {
    @JsonProperty
    private List<InntektAar> inntekter;
    @JsonProperty
    private String fnr;

    public List<InntektAar> getInntekter() {
        return inntekter;
    }

    public String getFnr() {
        return fnr;
    }
}
