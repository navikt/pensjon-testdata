package no.nav.pensjon.testdata.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.annotations.Api;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import no.nav.pensjon.testdata.configuration.support.JdbcTemplateWrapper;
import no.nav.pensjon.testdata.consumer.brev.BrevConsumer;
import no.nav.pensjon.testdata.consumer.brev.BrevMetaDataConsumer;
import no.nav.pensjon.testdata.consumer.brev.BrevMetadata;
import no.nav.pensjon.testdata.controller.support.BestillBrevRequest;
import no.nav.tjeneste.domene.pensjon.vedtaksbrev.binding.BestillAutomatiskBrevAdresseMangler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;

@RestController
@Api(tags = {"Brev"})
@SwaggerDefinition(tags = {
        @Tag(name = "", description = "Endepunkt for bestilling av automatiske brev i Pesys")
})
@RequestMapping("/api/brev")
public class BrevController {

    @Autowired
    private BrevConsumer brevConsumer;

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

    @PostMapping
    public ResponseEntity<HttpStatus> bestillBrev(@RequestBody BestillBrevRequest body) throws BestillAutomatiskBrevAdresseMangler {
        brevConsumer.bestillAutomatiskBrev(body);
        opprettBrevCounter.increment();
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<BrevMetadata>> hentBrevkoder(){
        return ResponseEntity.ok(brevMetaData.getAllBrev());
    }

}
