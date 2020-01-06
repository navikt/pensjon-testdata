package no.nav.pensjon.testdata.controller.moog;

import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public class FetchTestdataRequest {

    @ApiParam(name = "fom", value = "2019-10-29 13:40:00")
    private String fom;
    @ApiParam(name = "tom", value = "2019-10-29 13:50:00")
    private String tom;
    @ApiParam(name = "identer", value = "Identer som har gjennomført behandling")
    private List<String> identer;

    public String getFom() {
        return fom;
    }

    public void setFom(String fom) {
        this.fom = fom;
    }

    public String getTom() {
        return tom;
    }

    public void setTom(String tom) {
        this.tom = tom;
    }

    public List<String> getIdenter() {
        return identer;
    }

    public void setIdenter(List<String> identer) {
        this.identer = identer;
    }
}
