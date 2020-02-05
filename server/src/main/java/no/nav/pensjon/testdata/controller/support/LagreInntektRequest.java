package no.nav.pensjon.testdata.controller.support;

public class LagreInntektRequest {
    String fnr;
    Integer fomAar;
    Integer tomAar;
    Long belop;

    public String getFnr() {
        return fnr;
    }

    public void setFnr(String fnr) {
        this.fnr = fnr;
    }

    public Integer getFomAar() {
        return fomAar;
    }

    public void setFomAar(Integer fomAar) {
        this.fomAar = fomAar;
    }

    public Integer getTomAar() {
        return tomAar;
    }

    public void setTomAar(Integer tomAar) {
        this.tomAar = tomAar;
    }

    public Long getBelop() {
        return belop;
    }

    public void setBelop(Long belop) {
        this.belop = belop;
    }


}
