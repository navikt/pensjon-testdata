package no.nav.pensjon.testdata.controller;

public class FlyttSakRequest {
    private Long sakId;
    private Long nyEnhet;

    public Long getNyEnhet() {
        return nyEnhet;
    }

    public void setNyEnhet(Long nyEnhet) {
        this.nyEnhet = nyEnhet;
    }

    public Long getSakId() {
        return sakId;
    }

    public void setSakId(Long sakId) {
        this.sakId = sakId;
    }

}
