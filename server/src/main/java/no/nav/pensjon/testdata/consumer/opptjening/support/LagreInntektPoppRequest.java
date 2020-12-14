package no.nav.pensjon.testdata.consumer.opptjening.support;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LagreInntektPoppRequest {
    @JsonProperty
    private Inntekt inntekt;

    public LagreInntektPoppRequest(Inntekt inntekt) {
        this.inntekt = inntekt;
    }
}
