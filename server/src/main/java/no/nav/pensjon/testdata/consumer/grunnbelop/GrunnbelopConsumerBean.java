package no.nav.pensjon.testdata.consumer.grunnbelop;

import no.nav.pensjon.testdata.consumer.grunnbelop.support.SatsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class GrunnbelopConsumerBean {

    Logger logger = LoggerFactory.getLogger(GrunnbelopConsumerBean.class);

    private RestTemplate restTemplate = new RestTemplate();
    @Value("${preg.rest.veiet.grunnbelop.endpoint.url}")
    private String endpoint;

    private Map<Integer, Long> grunnbelopCache;

    private Integer STATIC_FOM_AAR = 1950;
    private Integer STATIC_TOM_AAR = 2050;

    public Map<Integer, Long> hentVeietGrunnbelop() throws IOException {


        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<SatsResponse> response = restTemplate.exchange(
                UriComponentsBuilder
                        .fromHttpUrl(endpoint + "?fomAr=" + STATIC_FOM_AAR + "&tomAr=" + STATIC_TOM_AAR)
                        .toUriString(),
                HttpMethod.GET,
                null,
                SatsResponse.class);

        Map<Integer, Long> satser = new HashMap<>();
        response
                .getBody()
                .getVeietSatsResultater()
                .stream()
                .forEach(sats -> satser.put(sats.getAr(), sats.getVerdi()));

        if (grunnbelopCache == null) {
            logger.debug("Fetched new grunnbelop, and stored to cache");
            grunnbelopCache = satser;
        } else {
            logger.debug("Used grunnbelop from existing cache");
        }

        return grunnbelopCache;
    }
}
