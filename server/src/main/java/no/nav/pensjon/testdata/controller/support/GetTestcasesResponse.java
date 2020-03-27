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
        private String navn;
        private List<String> begrensninger;

        public Testcase(String navn, List<String> begrensninger) {
            this.navn = navn;
            this.begrensninger = begrensninger;
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
    }
}
