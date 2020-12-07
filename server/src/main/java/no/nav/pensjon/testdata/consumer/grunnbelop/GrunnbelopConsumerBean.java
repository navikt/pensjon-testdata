package no.nav.pensjon.testdata.consumer.grunnbelop;

import no.nav.pensjon.testdata.consumer.grunnbelop.support.SatsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GrunnbelopConsumerBean {

    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${preg.rest.veiet.grunnbelop.endpoint.url}")
    private String endpoint;

    private final Map<Integer, Long> grunnbelopCache = new ConcurrentHashMap<>();

    public Map<Integer, Long> hentVeietGrunnbelop(){
        if (grunnbelopCache.isEmpty()){
            ResponseEntity<SatsResponse> response = restTemplate.exchange(
                    UriComponentsBuilder
                            .fromHttpUrl(endpoint + "?fomAr=1950&tomAr=2050")
                            .toUriString(),
                    HttpMethod.GET,
                    null,
                    SatsResponse.class);

            Objects.requireNonNull(response
                    .getBody())
                    .getVeietSatsResultater()
                    .forEach(sats -> grunnbelopCache.put(sats.getAr(), sats.getVerdi()));
        }
        return grunnbelopCache;
    }
}
