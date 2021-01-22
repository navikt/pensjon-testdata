package no.nav.pensjon.testdata.consumer.grunnbelop.support;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SatsResponse {
    @JsonProperty("veietSatsResultater")
    private List<VeietSatsResultat> veietSatsResultater;

    @JsonProperty("veietSatsResultater")
    public List<VeietSatsResultat> getVeietSatsResultater() {
        return veietSatsResultater;
    }

    @JsonProperty("veietSatsResultater")
    public void setVeietSatsResultater(List<VeietSatsResultat> veietSatsResultater) {
        this.veietSatsResultater = veietSatsResultater;
    }
}
