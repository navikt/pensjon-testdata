package no.nav.pensjon.testdata.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import no.nav.pensjon.testdata.configuration.support.Environment;
import no.nav.pensjon.testdata.consumer.opptjening.TestdataConsumerBean;
import no.nav.pensjon.testdata.controller.support.OpprettPersonRequest;
import no.nav.pensjon.testdata.controller.support.response.DollyResponse;
import no.nav.pensjon.testdata.controller.support.response.OpprettPersonRemoteRequest;
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
@Api(tags = {"Person"})
@SwaggerDefinition(tags = {
        @Tag(name = "", description = "Endepunkter for person")
})
public class PersonController {

    @Autowired
    private TestdataConsumerBean testdataConsumerBean;

    @RequestMapping(method = RequestMethod.POST, path = "/person")
    @ApiOperation(value = "Oppretter personer innenfor pensjonsområdet (PEN, POPP og SAM)")
    public ResponseEntity<DollyResponse> opprettPerson(
            @RequestHeader("Nav-Call-Id") String callId,
            @RequestHeader("Nav-Consumer-Id") String consumerId,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody OpprettPersonRequest request) {

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
                    OpprettPersonRemoteRequest remoteRequest = createRemoteRequest(request);
                    Response response = testdataConsumerBean.lagrePerson(
                            callId,
                            consumerId,
                            token,
                            avaiableEnvironments.get(miljo).getUrl(),
                            remoteRequest);

                    ResponseEnvironment env = new ResponseEnvironment();
                    env.setMiljo(miljo);
                    env.setResponse(response);
                    dollyResponse.addStatus(env);
                });

        return ResponseEntity.ok(dollyResponse);
    }

    private OpprettPersonRemoteRequest createRemoteRequest(OpprettPersonRequest request) {
        OpprettPersonRemoteRequest remoteRequest = new OpprettPersonRemoteRequest();
        remoteRequest.setFnr(request.getFnr());
        remoteRequest.setFodselsDato(request.getFodselsDato());
        remoteRequest.setDodsDato(request.getDodsDato());
        remoteRequest.setUtvandringsDato(request.getUtvandringsDato());
        remoteRequest.setBostedsland(request.getBostedsland());
        return remoteRequest;
    }

    private boolean erMiljoTilgjengelig(Map<String, Environment> avaiableEnvironments, String miljo) {
        return avaiableEnvironments.get(miljo) != null;
    }

}
