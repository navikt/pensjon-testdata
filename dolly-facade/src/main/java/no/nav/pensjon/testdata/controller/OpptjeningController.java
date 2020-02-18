package no.nav.pensjon.testdata.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import no.nav.pensjon.testdata.configuration.support.Environment;
import no.nav.pensjon.testdata.consumer.opptjening.TestdataConsumerBean;
import no.nav.pensjon.testdata.controller.support.LagreInntektRemoteRequest;
import no.nav.pensjon.testdata.controller.support.LagreInntektRequest;
import no.nav.pensjon.testdata.controller.support.response.DollyResponse;
import no.nav.pensjon.testdata.controller.support.response.Response;
import no.nav.pensjon.testdata.controller.support.response.ResponseEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static no.nav.pensjon.testdata.controller.support.EnvironmentResolver.getAvaiableEnvironments;

@RestController
@Api(tags = {"Opptjening"})
@SwaggerDefinition(tags = {
        @Tag(name = "", description = "Endepunkt som gjennomfører behandling av inntekter og opptjening mot POPP")
})
public class OpptjeningController {

    @Autowired
    private TestdataConsumerBean testdataConsumerBean;

    @RequestMapping(method = RequestMethod.POST, path = "/inntekt")
    public ResponseEntity<DollyResponse> lagreInntekt(
            @RequestHeader("Nav-Call-Id") String callId,
            @RequestHeader("Nav-Consumer-Id") String consumerId,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody LagreInntektRequest request)  {
        Map<String, Environment> avaiableEnvironments = getAvaiableEnvironments();
        for (String miljo : request.getMiljoer()) {
            if (!erMiljoTilgjengelig(avaiableEnvironments, miljo)) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ikke mulig å opprette testdata mot " + miljo, null);
            }
        }

        DollyResponse dollyResponse = new DollyResponse();
        request.getMiljoer()
                .stream()
                .parallel()
                .forEach(miljo -> {
                    LagreInntektRemoteRequest remoteRequest = createRemoteRequest(request);
                    Response response = testdataConsumerBean.lagreInntekt(
                            remoteRequest,
                            token,
                            avaiableEnvironments.get(miljo).getUrl(),
                            callId,
                            consumerId);

                    ResponseEnvironment env = new ResponseEnvironment();
                    env.setMiljo(miljo);
                    env.setResponse(response);
                    dollyResponse.addStatus(env);
                });
        return ResponseEntity.ok(dollyResponse);
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

    private boolean erMiljoTilgjengelig(Map<String, Environment> avaiableEnvironments, String miljo) {
        return avaiableEnvironments.get(miljo) != null;
    }
}
