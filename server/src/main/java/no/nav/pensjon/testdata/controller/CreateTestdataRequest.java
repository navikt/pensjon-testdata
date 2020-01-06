package no.nav.pensjon.testdata.controller;

import java.util.Map;

class CreateTestdataRequest {

    private String testCaseId;
    private Map<String,String> handlebars;
    private boolean opprettPerson;
    private String server;
    private String username;
    private String password;
    private String database;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

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
