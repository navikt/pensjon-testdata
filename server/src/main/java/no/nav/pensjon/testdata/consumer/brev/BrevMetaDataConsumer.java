package no.nav.pensjon.testdata.consumer.brev;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BrevMetaDataConsumer {
    @Value("${brevmetadata.endpoint.url}")
    private String brevMetaDataUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public List<BrevMetadata> getAllBrev() {
        ResponseEntity<BrevMetadata[]> response = restTemplate.getForEntity(
                UriComponentsBuilder.fromHttpUrl(brevMetaDataUrl + "/allBrev?includeXsd=false").toUriString(), BrevMetadata[].class);
        return Arrays.stream(Objects.requireNonNull(response.getBody()))
                .filter(m -> !m.isRedigerbart())
                .collect(Collectors.toList());
    }

}
