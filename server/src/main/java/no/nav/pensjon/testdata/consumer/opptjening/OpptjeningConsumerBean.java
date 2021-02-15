package no.nav.pensjon.testdata.consumer.opptjening;

import no.nav.pensjon.testdata.consumer.opptjening.support.LagreInntektPoppRequest;
import no.nav.pensjon.testdata.consumer.opptjening.support.POPPInternalFailureException;
import no.nav.pensjon.testdata.service.RetryLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class OpptjeningConsumerBean implements RetryLayer {

    Logger logger = LoggerFactory.getLogger(OpptjeningConsumerBean.class);

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${popp.endpoint.url}")
    private String poppEndpoint;

    public Boolean lagreInntekt(LagreInntektPoppRequest body, HttpHeaders httpHeaders) {
        HttpEntity restRequest;
        ResponseEntity<String> response;

        restRequest = new HttpEntity<>(body, httpHeaders);
        try {
            response = restTemplate.exchange(
                    UriComponentsBuilder.fromHttpUrl(poppEndpoint +"/inntekt").toUriString(),
                    HttpMethod.POST,
                    restRequest,
                    String.class);
        } catch (RestClientResponseException e) {
            String message = "Request to POPP/inntekt failed with msg: " + e.getMessage();
            logger.error(message, e);
            int rawStatusCode = e.getRawStatusCode();
            if (rawStatusCode == 401) {
                throw new RuntimeException("User is not authorized to use this service!", e);
            } else if (rawStatusCode == 512) {
                if (e.getMessage().contains("PersonDoesNotExistExceptionDto")) {
                    throw new RuntimeException(
                            "Person ikke funnet i POPP ", e);
                }
            }
            else if (rawStatusCode == 500){
                throw new POPPInternalFailureException(message, e);
            }
            throw new RuntimeException(message, e);
        }
        return response.getStatusCodeValue() == 200;
    }

    public Boolean lagrePerson(HttpHeaders httpHeaders) {
        HttpEntity restRequest;
        ResponseEntity<String> response;
        restRequest = new HttpEntity<>("", httpHeaders);
        try {
            response = restTemplate.exchange(
                    UriComponentsBuilder.fromHttpUrl(poppEndpoint + "/person").toUriString(),
                    HttpMethod.POST,
                    restRequest,
                    String.class);
        } catch (RestClientResponseException e) {
            String message = "Request to POPP/person failed with msg: " + e.getMessage();
            logger.error(message, e);
            if (e.getRawStatusCode() == 401) {
                throw new RuntimeException("User is not authorized to use this service!", e);
            }
            throw new RuntimeException(message, e);

        }
        return response.getStatusCodeValue() == 200;
    }

}
