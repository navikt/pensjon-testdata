package no.nav.pensjon.testdata.configuration.support;

import java.time.LocalDateTime;

public class SAMLResponse {

    private String accessToken;
    private String issuedTokenType;
    private String tokenType;
    private String decodedToken;
    private LocalDateTime expiresAt;

    /**
     * @return the original Base64 encoded token
     */
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getIssuedTokenType() {
        return issuedTokenType;
    }

    public void setIssuedTokenType(String issuedTokenType) {
        this.issuedTokenType = issuedTokenType;
    }

    /**
     * @return the XML saml assertion
     */
    public String getDecodedToken() {
        return decodedToken;
    }

    public void setDecodedToken(String decodedToken) {
        this.decodedToken = decodedToken;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    @Override
    public String toString() {
        return "SAMLResponse{tokenType='" + tokenType + '\'' +
                ", expiresAt=" + expiresAt +
                '}';
    }
}
