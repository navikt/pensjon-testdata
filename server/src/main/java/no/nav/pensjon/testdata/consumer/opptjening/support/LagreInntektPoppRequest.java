package no.nav.pensjon.testdata.consumer.opptjening.support;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.StringJoiner;

public class LagreInntektPoppRequest {
    @JsonProperty
    private Inntekt inntekt;

    public LagreInntektPoppRequest(Inntekt inntekt) {
        this.inntekt = inntekt;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", LagreInntektPoppRequest.class.getSimpleName() + "[", "]")
                .add("inntekt=" + inntekt)
                .toString();
    }
}
