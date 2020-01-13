package no.nav.pensjon.testdata.controller.support;

import java.util.Map;

public class CreateTestdataRequest {

    private String testCaseId;
    private Map<String,String> handlebars;
    private boolean opprettPerson;

    public String getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
    }

    public Map<String, String> getHandlebars() {
        return handlebars;
    }

    public void setHandlebars(Map<String, String> handlebars) {
        this.handlebars = handlebars;
    }

    public boolean getOpprettPerson() {
        return opprettPerson;
    }

    public void setOpprettPerson(boolean opprettPerson) {
        this.opprettPerson = opprettPerson;
    }
}
