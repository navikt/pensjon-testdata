package no.nav.pensjon.testdata.consumer.opptjening;

import no.nav.pensjon.testdata.consumer.usertoken.HentUserTokenBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OpptjeningConsumerBean {

    Logger logger = LoggerFactory.getLogger(OpptjeningConsumerBean.class);

    @Autowired
    HentUserTokenBean hentUserTokenBean;

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${popp.inntekt.endpoint.url}")
    private String endpoint;

    public Boolean lagreInntekt(String body) {
        HttpEntity restRequest;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("Authorization", "Bearer " + hentUserTokenBean.fetch().getAccessToken());
        httpHeaders.add("Nav-Call-Id", "pensjon-testdata");
        httpHeaders.add("Nav-Consumer-Id", "pensjon-testdata");
        ResponseEntity<String> response;

        restRequest = new HttpEntity<>(body, httpHeaders);
        try {
            response = restTemplate.exchange(
                    UriComponentsBuilder.fromHttpUrl(endpoint).toUriString(),
                    HttpMethod.POST,
                    restRequest,
                    String.class);
        } catch (RestClientResponseException e) {
            if (e.getRawStatusCode() == 401) {
                throw new RuntimeException("User is not authorized to use this service!", e);
            } else if (e.getRawStatusCode() == 512) {
                if (e.getMessage().contains("PersonDoesNotExistExceptionDto")) {
                    throw new RuntimeException(
                            "Person ikke funnet i POPP ", e);
                }
            }
            String functionalError = getFunctionalError(e.getResponseBodyAsString());

            if (functionalError != null) {
                throw new RuntimeException(functionalError, e);
            } else {
                throw new RuntimeException("Unexpected error while trying to save InntektListe to POPP", e);
            }


        }
        return response.getStatusCodeValue() == 200;
    }

    private String getFunctionalError(String body) {
        Matcher matcher = Pattern.compile("message=(?<msg>.+?),+.+?]").matcher(body);
        while(matcher.find()) {
            return matcher.group("msg");
        }
        return null;
    }
}
