package no.nav.pensjon.testdata.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import no.nav.pensjon.testdata.configuration.support.JdbcTemplateWrapper;
import no.nav.pensjon.testdata.consumer.brev.BrevConsumer;
import no.nav.pensjon.testdata.controller.support.BestillBrevRequest;
import no.nav.pensjon.testdata.repository.support.ComponentCode;
import no.nav.tjeneste.domene.pensjon.vedtaksbrev.binding.BestillAutomatiskBrevAdresseMangler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Api(tags = {"Brev"})
@SwaggerDefinition(tags = {
        @Tag(name = "", description = "Endepunkt for bestilling av automatiske brev i Pesys")
})
public class BrevController {

    @Autowired
    private BrevConsumer brevConsumer;

    @Autowired
    private JdbcTemplateWrapper jdbcTemplateWrapper;

    @RequestMapping(method = RequestMethod.POST, path = "/brev")
    public ResponseEntity<HttpStatus> bestillBrev(@RequestBody BestillBrevRequest body) throws BestillAutomatiskBrevAdresseMangler {
        brevConsumer.bestillAutomatiskBrev(body);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/brev")
    public ResponseEntity<Brev[]> hentBrevkoder()  {
        List<Map<String, Object>> result  = jdbcTemplateWrapper.queryForList(ComponentCode.PEN,"SELECT * FROM T_K_BATCHBREV");

        Brev[] alleBrev = result
                .stream()
                .map(entity -> new Brev((String) entity.get("K_BATCHBREV_ID"), (String) entity.get("INNHOLD")))
                .toArray(Brev[]::new);

        return ResponseEntity.ok(alleBrev);
    }

    public class Brev {
        private String kodeverdi;
        private String dekode;

        public Brev(String kodeverdi, String dekode) {
            this.kodeverdi = kodeverdi;
            this.dekode = dekode;
        }

        public String getKodeverdi() {
            return kodeverdi;
        }

        public void setKodeverdi(String kodeverdi) {
            this.kodeverdi = kodeverdi;
        }

        public String getDekode() {
            return dekode;
        }

        public void setDekode(String dekode) {
            this.dekode = dekode;
        }
    }
}
