package no.nav.pensjon.testdata.configuration.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import no.nav.pensjon.testdata.configuration.SecretUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Base64;

@Service
public class SAMLTokenProvider {
    private static final Logger LOG = LoggerFactory.getLogger(SAMLTokenProvider.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Value("${sts.endpoint.url}")
    private String endpointUrl;

    public SAMLResponse fetchSamlToken(){
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, getBasicAuthHeader());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> tokenRequest = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    UriComponentsBuilder.fromHttpUrl(endpointUrl).path("/rest/v1/sts/samltoken").toUriString(),
                    HttpMethod.GET,
                    tokenRequest,
                    String.class);
            return objectMapper.readValue(response.getBody(), SAMLResponse.class);
        } catch (RestClientResponseException | IOException e) {
            LOG.error("Feil ved henting av systembruker SAML-token", e);
            throw new RuntimeException("Feil ved henting av systembruker SAML-token", e);
        }
    }

    private String getBasicAuthHeader() {
        try {
            String username = SecretUtil.readSecret("srvpensjon/username");
            String password = SecretUtil.readSecret("srvpensjon/password");
            return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not fetch SAML-token from " + endpointUrl);
        }
    }


}
