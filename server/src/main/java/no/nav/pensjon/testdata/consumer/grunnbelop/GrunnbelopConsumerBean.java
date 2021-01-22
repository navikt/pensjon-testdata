package no.nav.pensjon.testdata.consumer.grunnbelop;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.micrometer.core.instrument.util.IOUtils;

import no.nav.pensjon.testdata.consumer.grunnbelop.support.SatsResponse;
import no.nav.pensjon.testdata.consumer.grunnbelop.support.VeietSatsResultat;

@Service
public class GrunnbelopConsumerBean {

    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${preg.rest.veiet.grunnbelop.endpoint.url}")
    private String endpoint;

    public Map<Integer, Long> hentVeietGrunnbelop() throws JsonProcessingException {
        try {
            ResponseEntity<SatsResponse> response = restTemplate.exchange(
                    UriComponentsBuilder
                            .fromHttpUrl(endpoint + "?fomAr=1950&tomAr=2050")
                            .toUriString(),
                    HttpMethod.GET,
                    null,
                    SatsResponse.class);

            return parseToMap(Objects.requireNonNull(response.getBody()));
        }
        catch(RestClientException e){
            //les fra fil
            String beloep = IOUtils.toString(this.getClass().getResourceAsStream("/grunnbeloep.json"));
            ObjectMapper mapper = new ObjectMapper();
            SatsResponse satsResponse = mapper.readValue(beloep, SatsResponse.class);
            return parseToMap(satsResponse);
        }
    }

    private Map<Integer, Long> parseToMap(SatsResponse response) {
        return response
                .getVeietSatsResultater()
                .stream().collect(Collectors.toMap(VeietSatsResultat::getAr, VeietSatsResultat::getVerdi));
    }
}
