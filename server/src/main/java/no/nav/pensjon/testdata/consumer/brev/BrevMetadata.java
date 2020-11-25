package no.nav.pensjon.testdata.consumer.brev;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BrevMetadata {
    private String kodeverdi;
    private String dekode;
    private String dokumentmalId;
    private boolean redigerbart;

    public boolean isRedigerbart() {
        return redigerbart;
    }

    public void setRedigerbart(boolean redigerbart) {
        this.redigerbart = redigerbart;
    }

    public String getDekode() {
        return dekode;
    }

    public void setDekode(String dekode) {
        this.dekode = dekode;
    }

    @JsonProperty("kodeverdi")
    public String getKodeverdi() {
        return kodeverdi;
    }

    @JsonProperty("brevkodeIBrevsystem")
    public void setKodeverdi(String kodeverdi) {
        this.kodeverdi = kodeverdi;
    }

    public String getDokumentmalId() {
        return dokumentmalId;
    }

    public void setDokumentmalId(String dokumentmalId) {
        this.dokumentmalId = dokumentmalId;
    }
}
