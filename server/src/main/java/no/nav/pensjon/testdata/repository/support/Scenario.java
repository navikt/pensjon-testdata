package no.nav.pensjon.testdata.repository.support;

import java.io.File;

public class Scenario {

    private String scenarioId;
    private String name;
    private String penFile;
    private String poppFile;
    private String personIdPen;
    private String personIdPopp;
    private File poppFileSrc;
    private File penFileSrc;

    public File getPenFileSrc() {
        return penFileSrc;
    }

    public void setPenFileSrc(File penFileSrc) {
        this.penFileSrc = penFileSrc;
    }

    public String getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(String scenarioId) {
        this.scenarioId = scenarioId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPenFile() {
        return penFile;
    }

    public void setPenFile(String penFile) {
        this.penFile = penFile;
    }

    public String getPoppFile() {
        return poppFile;
    }

    public void setPoppFile(String poppFile) {
        this.poppFile = poppFile;
    }

    public String getPersonIdPen() {
        return personIdPen;
    }

    public void setPersonIdPen(String personIdPen) {
        this.personIdPen = personIdPen;
    }

    public String getPersonIdPopp() {
        return personIdPopp;
    }

    public void setPersonIdPopp(String personIdPopp) {
        this.personIdPopp = personIdPopp;
    }

    public File getPoppFileSrc() {
        return poppFileSrc;
    }

    public void setPoppFileSrc(File poppFileSrc) {
        this.poppFileSrc = poppFileSrc;
    }
}
