package no.nav.pensjon.testdata.controller.internal;

import no.nav.pensjon.testdata.configuration.support.JdbcTemplateWrapper;
import no.nav.pensjon.testdata.repository.OracleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
public class DatabaseController {
    @Autowired
    OracleRepository oracleRepository;

    @Autowired
    JdbcTemplateWrapper jdbcTemplateWrapper;

    @GetMapping("api/testdata/canclear/")
    public ResponseEntity<Boolean> canDbBeCleared() throws IOException {
        if (oracleRepository.canDatabaseBeCleared()) {
            return ResponseEntity.ok(Boolean.TRUE);
        } else {
            return ResponseEntity.ok(Boolean.FALSE);
        }
    }

    @GetMapping("api/testdata/status")
    public Map<String, Boolean> status() {
        return Map.of(
                "PEN", jdbcTemplateWrapper.pingPEN(),
                "POPP", jdbcTemplateWrapper.pingPOPP(),
                "SAM", jdbcTemplateWrapper.pingSAM()
        );
    }
}
