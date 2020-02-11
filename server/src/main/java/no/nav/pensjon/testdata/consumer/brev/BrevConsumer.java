package no.nav.pensjon.testdata.consumer.brev;

import no.nav.pensjon.testdata.controller.support.BestillBrevRequest;
import no.nav.tjeneste.domene.pensjon.vedtaksbrev.binding.BestillAutomatiskBrevAdresseMangler;
import no.nav.tjeneste.domene.pensjon.vedtaksbrev.binding.Vedtaksbrev;
import no.nav.tjeneste.domene.pensjon.vedtaksbrev.meldinger.BestillAutomatiskBrevRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BrevConsumer {

    @Autowired
    private Vedtaksbrev vedtaksbrevSoapService;

    public void bestillAutomatiskBrev(BestillBrevRequest request) throws BestillAutomatiskBrevAdresseMangler {
        BestillAutomatiskBrevRequest soapRequest = new BestillAutomatiskBrevRequest();
        soapRequest.setSakId(request.getSakId().length() == 0 ? null  : request.getSakId());
        soapRequest.setVedtakId(request.getVedtakId().length() == 0 ? null : request.getVedtakId());
        soapRequest.setKravId(request.getKravId().length() == 0 ? null :request.getKravId());
        soapRequest.setGjelder(request.getGjelder());
        soapRequest.setMottaker(request.getMottaker());
        soapRequest.setInternBatchBrevkode(request.getInternBatchBrevkode());
        soapRequest.setSaksbehandlerNavn("Pensjon testdata");
        vedtaksbrevSoapService.bestillAutomatiskBrev(soapRequest);
    }
}
