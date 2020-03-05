package no.nav.pensjon.testdata.controller.support.response;

public class Inntekt {
    private int InntektAar;
    private long belop;

    public int getInntektAar() {
        return InntektAar;
    }

    public void setInntektAar(int inntektAar) {
        InntektAar = inntektAar;
    }

    public long getBelop() {
        return belop;
    }

    public void setBelop(long belop) {
        this.belop = belop;
    }
}

