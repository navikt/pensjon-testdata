package no.nav.pensjon.testdata.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import no.nav.pensjon.testdata.consumer.opptjening.TestdataConsumerBean;
import no.nav.pensjon.testdata.controller.support.OpprettPersonRemoteRequest;
import no.nav.pensjon.testdata.controller.support.OpprettPersonRequest;
import no.nav.pensjon.testdata.controller.support.response.ResponseAgregate;
import no.nav.pensjon.testdata.controller.support.response.Response;
import no.nav.pensjon.testdata.controller.support.response.ResponseEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static no.nav.pensjon.testdata.configuration.support.EnvironmentResolver.erAlleMiljoerTilgjengelig;
import static no.nav.pensjon.testdata.configuration.support.EnvironmentResolver.getAvaiableEnvironments;

@RestController
@Api(tags = {"Person"})
@SwaggerDefinition(tags = {
        @Tag(name = "", description = "Endepunkter for person")
})
public class PersonController {

    @Autowired
    private TestdataConsumerBean testdataConsumerBean;

    @RequestMapping(method = RequestMethod.POST, path = "/api/v1/person")
    @ApiOperation(value = "Oppretter personer innenfor pensjonsområdet (PEN, POPP og SAM)")
    public ResponseEntity<ResponseAgregate> opprettPerson(
            @RequestHeader("Nav-Call-Id") String callId,
            @RequestHeader("Nav-Consumer-Id") String consumerId,
            @RequestHeader(value = "Authorization") String token,
            @RequestBody OpprettPersonRequest request) {

        erAlleMiljoerTilgjengelig(request.getMiljoer());

        ResponseAgregate responseAgregate = new ResponseAgregate();
        request.getMiljoer()
                .stream()
                .parallel()
                .forEach(miljo -> {
                    OpprettPersonRemoteRequest remoteRequest = createRemoteRequest(request);
                    Response response = testdataConsumerBean.lagrePerson(
                            callId,
                            consumerId,
                            token,
                            getAvaiableEnvironments(envInputStream).get(miljo).getUrl(),
                            remoteRequest);

                    ResponseEnvironment env = new ResponseEnvironment();
                    env.setMiljo(miljo);
                    env.setResponse(response);
                    responseAgregate.addStatus(env);
                });

        return ResponseEntity.ok(responseAgregate);
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
}
