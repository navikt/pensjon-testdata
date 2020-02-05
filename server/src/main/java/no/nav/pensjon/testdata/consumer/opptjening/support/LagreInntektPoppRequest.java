package no.nav.pensjon.testdata.consumer.opptjening.support;

public class LagreInntektPoppRequest {

    private Inntekt inntekt;

    public LagreInntektPoppRequest(Inntekt inntekt) {
        this.inntekt = inntekt;
    }

    public Inntekt getInntekt() {
        return inntekt;
    }

    public void setInntekt(Inntekt inntekt) {
        this.inntekt = inntekt;
    }
}
