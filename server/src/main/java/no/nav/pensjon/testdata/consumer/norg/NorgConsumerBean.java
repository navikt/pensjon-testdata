package no.nav.pensjon.testdata.consumer.norg;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class NorgConsumerBean {
    private RestTemplate restTemplate = new RestTemplate();

    @Value("${norg.endpoint.url}")
    private String norgEndpoint ;

    public String hentEnheter() {
        HttpEntity restRequest;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));

        ResponseEntity<String> response;

        List<Fordeling>  fordelinger = new ArrayList<>();
        fordelinger.add(new Fordeling("PEN"));
        fordelinger.add(new Fordeling("UFO"));

        restRequest = new HttpEntity<>(fordelinger.toArray(), httpHeaders);
        try {
            response = restTemplate.exchange(
                    UriComponentsBuilder.fromHttpUrl(norgEndpoint + "/v1/arbeidsfordelinger?skjermet=false").toUriString(),
                    HttpMethod.POST,
                    restRequest,
                    String.class);
        } catch (RestClientResponseException e) {
            throw new RuntimeException("Unexpected error while trying to fetch enheter from NORG", e);
        }
        return response.getBody();
    }

    class Fordeling {
        private String tema;
        private String enhetNummer;

        public Fordeling() {
            super();
        }

        public Fordeling(String tema) {
            super();
            this.tema = tema;
        }

        public String getTema() {
            return tema;
        }
        public void setTema(String tema) {
            this.tema = tema;
        }

        public String getEnhetNummer() {
            return enhetNummer;
        }

        public void setEnhetNummer(String enhetNummer) {
            this.enhetNummer = enhetNummer;
        }
    }
}
