package no.nav.pensjon.testdata.consumer.opptjening;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pensjon.testdata.controller.support.LagreInntektRemoteRequest;
import no.nav.pensjon.testdata.controller.support.response.HttpStatus;
import no.nav.pensjon.testdata.controller.support.response.OpprettPersonRemoteRequest;
import no.nav.pensjon.testdata.controller.support.response.Response;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalTime;

@Service
public class TestdataConsumerBean {

    private RestTemplate restTemplate = new RestTemplate();

    ObjectMapper objectMapper = new ObjectMapper();

    public Response lagreInntekt(LagreInntektRemoteRequest request, String token, String endpoint, String callId, String consumerId)  {
        HttpEntity restRequest;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        if (token != null) {
            httpHeaders.add("Authorization", "Bearer " + token);
        }

        httpHeaders.add("Nav-Call-Id", callId);
        httpHeaders.add("Nav-Consumer-Id", consumerId);
        ResponseEntity<String> restResponse;

        Response response = new Response();
        HttpStatus httpStatus = new HttpStatus();
        response.setPath("/inntekt");
        response.setTimestamp(LocalTime.now());
        response.setHttpStatus(httpStatus);
        restRequest = new HttpEntity<>(getRequestBody(request), httpHeaders);
        try {
            restResponse = restTemplate.exchange(
                    UriComponentsBuilder.fromHttpUrl(endpoint +"/inntekt").toUriString(),
                    HttpMethod.POST,
                    restRequest,
                    String.class);

            httpStatus.setStatus(restResponse.getStatusCode().value());
            httpStatus.setReasonPhrase(restResponse.getStatusCode().getReasonPhrase());
        } catch (RestClientResponseException e) {
            httpStatus.setStatus(e.getRawStatusCode());
            httpStatus.setReasonPhrase(org.springframework.http.HttpStatus.resolve(e.getRawStatusCode()).getReasonPhrase());

            if (e.getRawStatusCode() == 401) {
                response.setMessage("User is not authorized to use this service!");
            } else if (e.getRawStatusCode() == 512) {
                response.setMessage("Person ikke funnet i POPP ");
            }
            response.setMessage(getFunctionalErrorMessage(e));
        }
        return response;
    }

    private String getRequestBody(Object request)  {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public Response lagrePerson(String callId, String consumerId, String token, String endpoint, OpprettPersonRemoteRequest request) {
        HttpEntity restRequest;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        if (token != null) {
            httpHeaders.add("Authorization", "Bearer " +  token);
        }

        httpHeaders.add("Nav-Call-Id", callId);
        httpHeaders.add("Nav-Consumer-Id", consumerId);
        ResponseEntity<String> restResponse;

        Response response = new Response();
        HttpStatus httpStatus = new HttpStatus();
        response.setPath("/person");
        response.setTimestamp(LocalTime.now());
        response.setHttpStatus(httpStatus);

        restRequest = new HttpEntity<>(getRequestBody(request), httpHeaders);
        try {
            restResponse = restTemplate.exchange(
                    UriComponentsBuilder.fromHttpUrl(endpoint + "/person").toUriString(),
                    HttpMethod.POST,
                    restRequest,
                    String.class);

            httpStatus.setStatus(restResponse.getStatusCode().value());
            httpStatus.setReasonPhrase(restResponse.getStatusCode().getReasonPhrase());
        } catch (RestClientResponseException e) {
            httpStatus.setStatus(e.getRawStatusCode());
            httpStatus.setReasonPhrase(org.springframework.http.HttpStatus.resolve(e.getRawStatusCode()).getReasonPhrase());

            if (e.getRawStatusCode() == 401) {
                response.setMessage("User is not authorized to use this service!");
            }
            response.setMessage(getFunctionalErrorMessage(e));

        }
        return response;
    }

    private String getFunctionalErrorMessage(RestClientResponseException e) {
        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return om.readValue(e.getResponseBodyAsByteArray(), CustomError.class).getMessage();
        } catch (Exception ex) {
            return "Unexpected error while trying to save to POPP";
        }
    }

    static class CustomError {
        @JsonProperty
        private String message;

        public CustomError() {
        super();
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

}
