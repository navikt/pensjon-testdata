package no.nav.pensjon.testdata.consumer.brev;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    public List<Metadata> getAllBrev() {
        ResponseEntity<Metadata[]> response = restTemplate.getForEntity(
                UriComponentsBuilder.fromHttpUrl(brevMetaDataUrl + "/allBrev?includeXsd=false").toUriString(), Metadata[].class);
        return Arrays.stream(Objects.requireNonNull(response.getBody()))
                .filter(m -> !m.isRedigerbart())
                .collect(Collectors.toList());
    }

    public static class Metadata {
        private String kodeverdi;
        private String dekode;
        private String dokumentmalId;
        private boolean redigerbart;

        public boolean isRedigerbart() {
            return redigerbart;
        }

        public void setRedigerbart(boolean redigerbart) {
            this.redigerbart = redigerbart;
        }

        public String getDekode() {
            return dekode;
        }

        public void setDekode(String dekode) {
            this.dekode = dekode;
        }

        @JsonProperty("kodeverdi")
        public String getKodeverdi() {
            return kodeverdi;
        }

        @JsonProperty("brevkodeIBrevsystem")
        public void setKodeverdi(String kodeverdi) {
            this.kodeverdi = kodeverdi;
        }

        public String getDokumentmalId() {
            return dokumentmalId;
        }

        public void setDokumentmalId(String dokumentmalId) {
            this.dokumentmalId = dokumentmalId;
        }
    }
}
