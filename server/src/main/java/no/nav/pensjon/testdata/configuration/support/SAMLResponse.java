package no.nav.pensjon.testdata.configuration.support;

import java.util.Base64;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SAMLResponse {

    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("issued_token_type")
    private String issuedTokenType;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonIgnore
    private String decodedToken;
    @JsonProperty("expires_in")
    private long expiresIn;

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
        return new String(Base64.getDecoder().decode(accessToken.getBytes()));
    }

    public void setDecodedToken(String decodedToken) {
        this.decodedToken = decodedToken;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public boolean isExpired() {
        return expiresIn > 0;
    }

    @Override
    public String toString() {
        return "SAMLResponse{tokenType='" + tokenType + '\'' +
                ", expiresIn=" + expiresIn +
                '}';
    }
}
