package no.nav.pensjon.testdata.consumer.brev;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pensjon.testdata.controller.BrevMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;
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

        for (BatchBrevTypeCode batchCode : BatchBrevTypeCode.values()) {
            try {
                ResponseEntity<String> response = restTemplate.exchange(
                        UriComponentsBuilder.fromHttpUrl(brevMetaDataUrl + "/brevForBrevkode/" + batchCode).toUriString(),
                        HttpMethod.GET,
                        null,
                        String.class);

                brevdataList.add(BrevdataMapper.mapBrev(batchCode.name(), objectMapper.readValue(response.getBody(), Map.class)));
            }
            catch(ResourceAccessException ex){
                logger.error("Could not fetch brev-kode", ex);
                throw ex;
            }
            catch (Exception e) {
                logger.warn("Missing brev code " + batchCode);
            }
        }
        return brevdataList;
    }
}
