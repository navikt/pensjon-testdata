package no.nav.pensjon.testdata.controller.internal;

import no.nav.pensjon.testdata.repository.OracleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class DatabaseController {
    @Autowired
    OracleRepository oracleRepository;

    @GetMapping("/testdata/canclear/")
    public ResponseEntity<Boolean> canDbBeCleared() throws IOException {
        if (oracleRepository.canDatabaseBeCleared()) {
            return ResponseEntity.ok(Boolean.TRUE);
        } else {
            return ResponseEntity.ok(Boolean.FALSE);
        }
    }
}
