package no.nav.pensjon.testdata.consumer.opptjening.support;

import java.util.StringJoiner;

public class Inntekt {
    private String fnr;

    private String kilde = "PEN";

    private String kommune = "1337";

    private Integer inntektAr;

    private Long belop;

    private ChangeStamp changeStamp;

    private String inntektType;

    public Inntekt(String fnr, Integer inntektAr, Long belop, String inntektType) {
        this.fnr = fnr;
        this.inntektAr = inntektAr;
        this.belop = belop;
        this.inntektType = inntektType;
        this.changeStamp = new ChangeStamp();
    }

    public String getFnr() {
        return fnr;
    }

    public void setFnr(String fnr) {
        this.fnr = fnr;
    }

    public String getKilde() {
        return kilde;
    }

    public void setKilde(String kilde) {
        this.kilde = kilde;
    }

    public String getKommune() {
        return kommune;
    }

    public void setKommune(String kommune) {
        this.kommune = kommune;
    }

    public Integer getInntektAr() {
        return inntektAr;
    }

    public void setInntektAr(Integer inntektAr) {
        this.inntektAr = inntektAr;
    }

    public Long getBelop() {
        return belop;
    }

    public void setBelop(Long belop) {
        this.belop = belop;
    }

    public String getInntektType() {
        return inntektType;
    }

    public void setInntektType(String inntektType) {
        this.inntektType = inntektType;
    }

    public ChangeStamp getChangeStamp() {
        return changeStamp;
    }

    public void setChangeStamp(ChangeStamp changeStamp) {
        this.changeStamp = changeStamp;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Inntekt.class.getSimpleName() + "[", "]")
                .add("fnr='" + fnr + "'")
                .add("kilde='" + kilde + "'")
                .add("kommune='" + kommune + "'")
                .add("inntektAr=" + inntektAr)
                .add("belop=" + belop)
                .add("changeStamp=" + changeStamp)
                .add("inntektType='" + inntektType + "'")
                .toString();
    }
}
