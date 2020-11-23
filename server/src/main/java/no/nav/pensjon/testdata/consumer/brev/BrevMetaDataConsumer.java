package no.nav.pensjon.testdata.consumer.brev;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pensjon.testdata.controller.BrevMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BrevMetaDataConsumer {
    private static final Logger logger = LoggerFactory.getLogger(BrevMetaDataConsumer.class);

    @Value("${brevmetadata.endpoint.url}")
    private String brevMetaDataUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<BrevMetaData> getAllBrev() {
        List<BrevMetaData> brevdataList = new ArrayList<>();

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    UriComponentsBuilder.fromHttpUrl(brevMetaDataUrl + "/allBrev?includeXsd=false").toUriString(),
                    HttpMethod.GET,
                    null,
                    String.class);

            List<Map<String, Object>> brevs = objectMapper.readValue(response.getBody(), List.class);

            for (Map<String, Object> brev : brevs) {
                final BrevMetaData brevData = BrevdataMapper.mapBrev(brev);
                if (!brevData.getRedigerbart()) {
                    brevdataList.add(brevData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return brevdataList;
    }
}
