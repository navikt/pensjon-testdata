package no.nav.pensjon.testdata.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GetTestcasesResponse {

    List<Testcase> data;

    public GetTestcasesResponse(List<String> input) {
        this.data = new ArrayList<>();
        data.addAll(input.stream()
                .map(Testcase::new)
                .collect(Collectors.toList()));
    }

    public List<Testcase> getData() {
        return data;
    }

    public void setData(List<Testcase> data) {
        this.data = data;
    }



    private class Testcase {
        private String navn;

        public Testcase(String navn) {
            this.navn = navn;
        }

        public String getNavn() {
            return navn;
        }

        public void setNavn(String navn) {
            this.navn = navn;
        }
    }
}
