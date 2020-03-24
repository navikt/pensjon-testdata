package no.nav.pensjon.testdata.controller.support;

import javax.validation.constraints.NotEmpty;
import java.util.List;

public class LagreInntektRequest {
    @NotEmpty(message = "Paakrevd fnr")
    private String fnr;
    @NotEmpty(message = "Paakrevd fom år")
    private Integer fomAar;
    @NotEmpty(message = "Paakrevd tom år")
    private Integer tomAar;
    @NotEmpty(message = "Paakrevd beløp")
    private Long belop;
    boolean redusertMedGrunnbelop;

    @NotEmpty(message = "Må angi miljø")
    private List<String> miljoer;

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

    public List<String> getMiljoer() {
        return miljoer;
    }

    public void setMiljoer(List<String> miljoer) {
        this.miljoer = miljoer;
    }
}
