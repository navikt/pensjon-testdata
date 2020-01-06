package no.nav.pensjon.testdata.controller.moog;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
public class RemoteMoogController {

    RestTemplate restTemplate = new RestTemplate();

    @RequestMapping(method = RequestMethod.POST, path = "moog/testdata")
    public ResponseEntity<List> fetchTestdata(@RequestBody FetchTestdataRequest request) {
        ResponseEntity<List<String>> response =
                restTemplate.exchange("http://d26dbvl010.test.local:8080/testdata",
                        HttpMethod.POST,
                        new HttpEntity<>(request),
                        new ParameterizedTypeReference<List<String>>() {
                        });
        return ResponseEntity.ok(response.getBody());
    }

    @PostMapping(path = "moog/person/{fnr}")
    public ResponseEntity<HttpStatus> opprettPerson(@PathVariable String fnr) {
        String url = "http://d26dbvl010.test.local:8080/person/" + fnr;
        String response = restTemplate.postForObject(url, null, String.class);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
