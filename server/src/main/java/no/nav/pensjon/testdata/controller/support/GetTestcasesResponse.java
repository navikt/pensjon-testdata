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
        private String id;
        private String fritekstbeskrivelse;
        private String navn;
        private List<String> begrensninger;

        public Testcase(String id, String navn, List<String> begrensninger, String fritekstbeskrivelse) {
            this.id = id;
            this.fritekstbeskrivelse = fritekstbeskrivelse;
            this.navn = navn;
            this.begrensninger = begrensninger;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNavn() {
            return navn;
        }

        public void setNavn(String navn) {
            this.navn = navn;
        }

        public List<String> getBegrensninger() {
            return begrensninger;
        }

        public void setBegrensninger(List<String> begrensninger) {
            this.begrensninger = begrensninger;
        }

        public String getFritekstbeskrivelse() {
            return fritekstbeskrivelse;
        }

        public void setFritekstbeskrivelse(String fritekstbeskrivelse) {
            this.fritekstbeskrivelse = fritekstbeskrivelse;
        }
    }
}
