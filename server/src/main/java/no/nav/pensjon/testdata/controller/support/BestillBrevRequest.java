package no.nav.pensjon.testdata.controller.support;

public class BestillBrevRequest {

    private String vedtakId;
    private String internBatchBrevkode;
    private String kravId;
    private String sakId;
    private String gjelder;
    private String mottaker;

    public BestillBrevRequest() {
        super();
    }

    public String getVedtakId() {
        return vedtakId;
    }

    public void setVedtakId(String vedtakId) {
        this.vedtakId = vedtakId;
    }

    public String getInternBatchBrevkode() {
        return internBatchBrevkode;
    }

    public void setInternBatchBrevkode(String internBatchBrevkode) {
        this.internBatchBrevkode = internBatchBrevkode;
    }

    public String getKravId() {
        return kravId;
    }

    public void setKravId(String kravId) {
        this.kravId = kravId;
    }

    public String getSakId() {
        return sakId;
    }

    public void setSakId(String sakId) {
        this.sakId = sakId;
    }

    public String getGjelder() {
        return gjelder;
    }

    public void setGjelder(String gjelder) {
        this.gjelder = gjelder;
    }

    public String getMottaker() {
        return mottaker;
    }

    public void setMottaker(String mottaker) {
        this.mottaker = mottaker;
    }
}
