package no.nav.pensjon.testdata.controller.support;

import java.util.List;

public class ComprehensiveTestcase {

    private int id;
    private String fritekstbeskrivelse;
    private String navn;
    private String saksType;
    private String maaVaereFoedtIAarMaaned;
    private List<Handlebar> handlebars;

    public ComprehensiveTestcase(GetTestcasesResponse.Testcase t, List<Handlebar> testcaseHandlebars) {
        this.id = t.getId();
        this.navn = t.getNavn();
        this.fritekstbeskrivelse = t.getFritekstbeskrivelse();
        this.saksType = t.getSaksType();
        this.maaVaereFoedtIAarMaaned = t.getMaaVaereFoedtIAarMaaned();
        this.handlebars = testcaseHandlebars;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public String getFritekstbeskrivelse() {
        return fritekstbeskrivelse;
    }

    public void setFritekstbeskrivelse(String fritekstbeskrivelse) {
        this.fritekstbeskrivelse = fritekstbeskrivelse;
    }

    public String getSaksType() {
        return saksType;
    }

    public void setSaksType(String saksType) {
        this.saksType = saksType;
    }

    public String getMaaVaereFoedtIAarMaaned() {
        return maaVaereFoedtIAarMaaned;
    }

    public void setMaaVaereFoedtIAarMaaned(String maaVaereFoedtIAarMaaned) {
        this.maaVaereFoedtIAarMaaned = maaVaereFoedtIAarMaaned;
    }

    public List<Handlebar> getHandlebars() {
        return handlebars;
    }

    public void setHandlebars(List<Handlebar> handlebars) {
        this.handlebars = handlebars;
    }
}
