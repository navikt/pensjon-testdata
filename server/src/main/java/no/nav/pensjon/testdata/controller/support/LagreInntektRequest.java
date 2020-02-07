package no.nav.pensjon.testdata.controller.support;

public class LagreInntektRequest {
    private String fnr;
    private Integer fomAar;
    private Integer tomAar;
    private Long belop;
    boolean redusertMedGrunnbelop;

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

    public boolean isRedusertMedGrunnbelop() {
        return redusertMedGrunnbelop;
    }

    public void setRedusertMedGrunnbelop(boolean redusertMedGrunnbelop) {
        this.redusertMedGrunnbelop = redusertMedGrunnbelop;
    }

    public String toString() {
        return "Fom: " + fomAar + " Tom: " + tomAar + " Belop: " + belop + " Nedjustering: " + redusertMedGrunnbelop;
    }
}
