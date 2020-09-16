package no.nav.pensjon.testdata.consumer.brev;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pensjon.testdata.controller.BrevMetaData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BrevMetaDataConsumer {

    @Value("${brevmetadata.endpoint.url}")
    private String brevMetaDataUrl;

    private RestTemplate restTemplate = new RestTemplate();
    private ObjectMapper objectMapper = new ObjectMapper();

    public List<BrevMetaData> getAllBrev() throws IOException {
        List<BrevMetaData> brevdataList = new ArrayList<>();

        for (BatchBrevTypeCode batchCode : BatchBrevTypeCode.values()) {
            try {
                ResponseEntity<String> response = restTemplate.exchange(
                        UriComponentsBuilder.fromHttpUrl(brevMetaDataUrl + "/brevForBrevkode/" + batchCode).toUriString(),
                        HttpMethod.GET,
                        null,
                        String.class);

                Map<String, Object> metaData = objectMapper.readValue(response.getBody(), Map.class);
                brevdataList.add(BrevdataMapper.mapBrev(objectMapper.readValue(response.getBody(), Map.class)));
            } catch (Exception e) {
                System.out.println("missing code " + batchCode);
            }
        }
        return brevdataList;
    }

}
