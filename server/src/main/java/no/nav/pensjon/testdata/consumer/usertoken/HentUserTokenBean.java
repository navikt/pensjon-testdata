package no.nav.pensjon.testdata.consumer.usertoken;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pensjon.testdata.configuration.SecretUtil;
import no.nav.pensjon.testdata.consumer.usertoken.support.UserToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Base64;

@Service
public class HentUserTokenBean {
    private String endpointUrl = "https://security-token-service.nais.preprod.local";
    private String username;
    private String password;

    private RestTemplate restTemplate = new RestTemplate();

    Logger logger = LoggerFactory.getLogger(HentUserTokenBean.class);

    public UserToken fetch() {
        logger.trace("Retrieving JWT token for service user");
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, getBasicAuthHeader());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity restRequest = new HttpEntity<>(headers);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(endpointUrl)
                .path("/rest/v1/sts/token")
                .queryParam("grant_type", "client_credentials")
                .queryParam("scope", "openid");

        UserToken hentUserTokenResponse;
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    uriBuilder.toUriString(),
                    HttpMethod.GET,
                    restRequest,
                    String.class);
            hentUserTokenResponse = getUserToken(response.getBody());
        } catch (HttpStatusCodeException e) {
            logger.error("Feil ved henting av SystembrukerToken: " + e.getMessage(), e);
            throw new RuntimeException("Feil ved henting av SystembrukerToken: " + e.getMessage(), e);
        }
        return hentUserTokenResponse;
    }

    private UserToken getUserToken(String jsonResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jsonResponse, UserToken.class);
        } catch (IOException e) {
            throw new RuntimeException("Feil ved mapping av UserToken: " + e.getMessage(), e);
        }
    }

    private String getBasicAuthHeader() {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

    @PostConstruct
    public void init() throws IOException {
        this.username = SecretUtil.readSecret("srvpensjon/username");
        this.password = SecretUtil.readSecret("srvpensjon/password");
    }

}
