package no.nav.pensjon.testdata.controller.support;

public class InntektPOPP {
    private String miljo;
    private String fnr;
    private int inntektAar;
    private long belop;

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

    public int getInntektAar() {
        return inntektAar;
    }

    public void setInntektAar(int inntektAar) {
        this.inntektAar = inntektAar;
    }

    public long getBelop() {
        return belop;
    }

    public void setBelop(long belop) {
        this.belop = belop;
    }
}
