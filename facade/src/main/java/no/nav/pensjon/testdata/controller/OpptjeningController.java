package no.nav.pensjon.testdata.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import no.nav.pensjon.testdata.consumer.opptjening.TestdataConsumerBean;
import no.nav.pensjon.testdata.controller.support.LagreInntektRemoteRequest;
import no.nav.pensjon.testdata.controller.support.LagreInntektRequest;
import no.nav.pensjon.testdata.controller.support.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

import static no.nav.pensjon.testdata.configuration.support.EnvironmentResolver.erAlleMiljoerTilgjengelig;
import static no.nav.pensjon.testdata.configuration.support.EnvironmentResolver.getAvaiableEnvironments;

@RestController
@Api(tags = {"Opptjening"})
@SwaggerDefinition(tags = {
        @Tag(name = "", description = "Endepunkt som gjennomfører distribusjon av inntekter og opptjening til POPP")
})
public class OpptjeningController {

    @Autowired
    private TestdataConsumerBean testdataConsumerBean;

    @RequestMapping(method = RequestMethod.POST, path = "/api/v1/inntekt")
    public ResponseEntity<ResponseAgregate> lagreInntekt(
            @RequestHeader("Nav-Call-Id") String callId,
            @RequestHeader("Nav-Consumer-Id") String consumerId,
            @RequestHeader(value = "Authorization") String token,
            @RequestBody LagreInntektRequest request) {

        erAlleMiljoerTilgjengelig(request.getMiljoer());

        ResponseAgregate responseAgregate = new ResponseAgregate();
        request.getMiljoer()
                .stream()
                .parallel()
                .forEach(miljo -> {
                    LagreInntektRemoteRequest remoteRequest = createRemoteRequest(request);
                    Response response = testdataConsumerBean.lagreInntekt(
                            remoteRequest,
                            token,
                            getAvaiableEnvironments().get(miljo).getUrl(),
                            callId,
                            consumerId);

                    ResponseEnvironment env = new ResponseEnvironment();
                    env.setMiljo(miljo);
                    env.setResponse(response);
                    responseAgregate.addStatus(env);
                });
        return ResponseEntity.ok(responseAgregate);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/api/v1/inntekt")
    public ResponseEntity<Inntekter> hentInntekt(
            @RequestHeader("Nav-Call-Id") String callId,
            @RequestHeader("Nav-Consumer-Id") String consumerId,
            @RequestHeader(value = "Authorization") String token,
            @RequestParam String fnr,
            @RequestParam String miljo) {
        Inntekter inntektliste = new Inntekter();

        erAlleMiljoerTilgjengelig(Collections.singletonList(miljo));
        List<Inntekt> inntekter = testdataConsumerBean.hentInntekt(
                fnr,
                miljo,
                token,
                getAvaiableEnvironments().get(miljo).getUrl(),
                callId,
                consumerId);
        inntektliste.setInntekter(inntekter);
        inntektliste.setFnr(fnr);
        inntektliste.setMiljo(miljo);
        return ResponseEntity.ok(inntektliste);
    }


    private LagreInntektRemoteRequest createRemoteRequest(@RequestBody LagreInntektRequest request) {
        LagreInntektRemoteRequest remoteRequest = new LagreInntektRemoteRequest();
        remoteRequest.setFnr(request.getFnr());
        remoteRequest.setFomAar(request.getFomAar());
        remoteRequest.setTomAar(request.getTomAar());
        remoteRequest.setBelop(request.getBelop());
        remoteRequest.setRedusertMedGrunnbelop(request.isRedusertMedGrunnbelop());
        return remoteRequest;
    }
}
