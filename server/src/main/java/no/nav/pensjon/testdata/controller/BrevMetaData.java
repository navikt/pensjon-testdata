package no.nav.pensjon.testdata.controller;

public class BrevMetaData {
    private String kodeverdi;
    private String dekode;

    public BrevMetaData(String kodeverdi, String dekode) {
        this.kodeverdi = kodeverdi;
        this.dekode = dekode;
    }

    public String getKodeverdi() {
        return kodeverdi;
    }

    public void setKodeverdi(String kodeverdi) {
        this.kodeverdi = kodeverdi;
    }

    public String getDekode() {
        return dekode;
    }

    public void setDekode(String dekode) {
        this.dekode = dekode;
    }
}
