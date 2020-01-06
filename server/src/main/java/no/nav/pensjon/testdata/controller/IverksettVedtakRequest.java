package no.nav.pensjon.testdata.controller;

public class IverksettVedtakRequest {

    private Long vedtakId;
    private String server;
    private String username;
    private String password;
    private String database;

    public Long getVedtakId() {
        return vedtakId;
    }

    public void setVedtakId(Long vedtakId) {
        this.vedtakId = vedtakId;
    }

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

}
