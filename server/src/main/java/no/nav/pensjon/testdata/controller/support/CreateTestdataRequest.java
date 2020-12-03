package no.nav.pensjon.testdata.controller.support;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class CreateTestdataRequest {
    @JsonProperty
    private int testCaseId;
    @JsonProperty
    private Map<String,String> handlebars;

    public int getTestCaseId() {
        return testCaseId;
    }

    public Map<String, String> getHandlebars() {
        return handlebars;
    }
}
