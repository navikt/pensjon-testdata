package no.nav.pensjon.testdata.controller.support.response;

import java.util.ArrayList;
import java.util.List;

public class DollyResponse {

    private List<ResponseEnvironment> status = new ArrayList<>();

    public List<ResponseEnvironment> getStatus() {
        return status;
    }

    public void setStatus(List<ResponseEnvironment> status) {
        this.status = status;
    }

    public void addStatus(ResponseEnvironment env) {
        this.status.add(env);
    }
}
