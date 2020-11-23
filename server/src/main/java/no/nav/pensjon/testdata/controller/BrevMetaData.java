package no.nav.pensjon.testdata.controller;

public class BrevMetaData {
    private String kodeverdi;
    private String dekode;
    private String dokumentmalId;
    private Boolean redigerbart;

    public BrevMetaData(String kodeverdi, String dekode, String dokumentmalId, Boolean redigerbart) {
        this.kodeverdi = kodeverdi;
        this.dekode = dekode;
        this.dokumentmalId = dokumentmalId;
        this.redigerbart = redigerbart;
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

    public String getDokumentmalId() {
        return dokumentmalId;
    }

    public void setDokumentmalId(String dokumentmalId) {
        this.dokumentmalId = dokumentmalId;
    }

    public Boolean getRedigerbart() {
        return redigerbart;
    }

    public void setRedigerbart(Boolean redigerbart) {
        this.redigerbart = redigerbart;
    }
}
