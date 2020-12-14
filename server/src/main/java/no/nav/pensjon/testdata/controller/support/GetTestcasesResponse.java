package no.nav.pensjon.testdata.controller.support;

import java.util.List;

public class GetTestcasesResponse {

    List<Testcase> data;

    public GetTestcasesResponse(List<Testcase> data) {
        this.data = data;
    }

    public List<Testcase> getData() {
        return data;
    }

    public void setData(List<Testcase> data) {
        this.data = data;
    }

    public static class Testcase {
        private int id;
        private String fritekstbeskrivelse;
        private String navn;
        private String saksType;
        private String maaVaereFoedtIAarMaaned;

        public Testcase(int id, String navn, String fritekstbeskrivelse, String saksType, String maaVaereFoedtIAarMaaned) {
            this.id = id;
            this.navn = navn;
            this.fritekstbeskrivelse = fritekstbeskrivelse;
            this.saksType = saksType;
            this.maaVaereFoedtIAarMaaned = maaVaereFoedtIAarMaaned;
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
    }
}
