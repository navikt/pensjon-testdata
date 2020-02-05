package no.nav.pensjon.testdata.consumer.usertoken.support;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class UserToken {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("token_type")
    private String tokenType;

    private final LocalDateTime issuedTime = LocalDateTime.now();

    public UserToken() {
        //Required by Jackson when mapping json object
    }


    public UserToken(String accessToken, Long expiresIn, String tokenType) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.tokenType = tokenType;
    }

    /**
     *
     * @param expirationLeeway the amount of seconds to be subtracted from the expirationTime to avoid returning false positives
     * @return <code>true</code> if "now" is after the expirationtime(minus leeway), else returns <code>false</code>
     */
    public boolean isExpired(long expirationLeeway) {
        return LocalDateTime.now().isAfter(issuedTime.plusSeconds(expiresIn).minusSeconds(expirationLeeway));
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public String getTokenType() {
        return tokenType;
    }

    @Override
    public String toString() {
        return "UserToken{" +
                "accessToken='" + accessToken + '\'' +
                ", expiresIn=" + expiresIn +
                ", tokenType='" + tokenType + '\'' +
                '}';
    }
}
