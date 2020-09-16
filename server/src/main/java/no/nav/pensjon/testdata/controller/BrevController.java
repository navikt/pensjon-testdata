package no.nav.pensjon.testdata.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.annotations.Api;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import no.nav.pensjon.testdata.configuration.support.JdbcTemplateWrapper;
import no.nav.pensjon.testdata.consumer.brev.BrevConsumer;
import no.nav.pensjon.testdata.consumer.brev.BrevMetaDataConsumer;
import no.nav.pensjon.testdata.controller.support.BestillBrevRequest;
import no.nav.tjeneste.domene.pensjon.vedtaksbrev.binding.BestillAutomatiskBrevAdresseMangler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

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

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    private BrevMetaDataConsumer brevMetaData;

    private static Counter opprettBrevCounter;

    @PostConstruct
    private void initCounters() {
        opprettBrevCounter = Counter
                .builder("pensjon.testdata.bestill.brev.total")
                .description("Brevbestilling via testdataløsning")
                .register(meterRegistry);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/brev")
    public ResponseEntity<HttpStatus> bestillBrev(@RequestBody BestillBrevRequest body) throws BestillAutomatiskBrevAdresseMangler {
        brevConsumer.bestillAutomatiskBrev(body);
        opprettBrevCounter.increment();
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/brev")
    public ResponseEntity<List<BrevMetaData>> hentBrevkoder() throws IOException {
        return ResponseEntity.ok(brevMetaData.getAllBrev());
    }

}
