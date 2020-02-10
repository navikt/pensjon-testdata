package no.nav.pensjon.testdata.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.annotations.Api;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import no.nav.pensjon.testdata.consumer.opptjening.OpptjeningConsumerBean;
import no.nav.pensjon.testdata.controller.support.OpprettPersonRequest;
import no.nav.pensjon.testdata.repository.OracleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@Api(tags = {"Person"})
@SwaggerDefinition(tags = {
        @Tag(name = "", description = "Endepunkter for person")
})
public class PersonController {

    Logger logger = LoggerFactory.getLogger(PersonController.class);

    @Autowired
    private OpptjeningConsumerBean opptjeningConsumerBean;

    @Autowired
    private JdbcTemplate penJdbcTemplate;

    @Autowired
    @Qualifier("samJdbcTemplate")
    private JdbcTemplate samJdbcTemplate;

    @Autowired
    private OracleRepository oracleRepository;

    @Autowired
    private MeterRegistry meterRegistry;

    private Counter dollyLagrePersonCounter;
    private Counter dollyLagrePersonFailedCounter;

    @PostConstruct
    private void initCounters() {
        dollyLagrePersonCounter = Counter
                .builder("pensjon.testdata.lagret.person.fra.dolly.total")
                .description("Person lagret til PEN, POPP og SAM")
                .register(meterRegistry);
        dollyLagrePersonFailedCounter = Counter
                .builder("pensjon.testdata.lagret.person.fra.dolly.feilet.total")
                .description("Lagring av person til PEN, POPP og SAM feilet!")
                .register(meterRegistry);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/person")
    @Transactional
    public ResponseEntity<HttpStatus> opprettPerson(
            @RequestHeader("Nav-Call-Id") String callId,
            @RequestHeader("Nav-Consumer-Id") String consumerId,
            @RequestBody OpprettPersonRequest body) {

        oracleRepository.alterSession();

        try {
            createPenPerson(body);
            createSamPerson(body);
            opptjeningConsumerBean.lagrePerson(callId, consumerId, body.getFnr());
            dollyLagrePersonCounter.increment();
        } catch (Exception e) {
            dollyLagrePersonFailedCounter.increment();
            throw e;
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    private void createSamPerson(OpprettPersonRequest request) {
        String personPreparedStatement = "INSERT INTO T_PERSON " +
                "(FNR_FK, " +
                "DATO_OPPRETTET, " +
                "OPPRETTET_AV, " +
                "DATO_ENDRET, " +
                "ENDRET_AV, " +
                "VERSJON) " +
                "VALUES (?,?,?,?,?,?)";
        
        samJdbcTemplate.execute(personPreparedStatement, (PreparedStatementCallback<Boolean>) preparedStatement -> {
            preparedStatement.setString(1, request.getFnr());
            preparedStatement.setDate(2, getSqlDate(LocalDate.now().toEpochDay()));
            preparedStatement.setString(3, "PENSJON-TESTDATA");
            preparedStatement.setDate(4, getSqlDate(LocalDate.now().toEpochDay()));
            preparedStatement.setString(5, "PENSJON-TESTDATA");
            preparedStatement.setInt(6, 0);
            return preparedStatement.execute();
        });
    }

    private void createPenPerson(OpprettPersonRequest request) {
        String personPreparedStatement = "INSERT INTO T_PERSON " +
                "(FNR_FK, " +
                "DATO_FODSEL, " +
                "DATO_DOD, " +
                "DATO_UTVANDRET, " +
                "BOSTEDSLAND, " +
                "DATO_OPPRETTET, " +
                "OPPRETTET_AV, " +
                "DATO_ENDRET, " +
                "ENDRET_AV, " +
                "VERSJON) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?)";

        java.sql.Date fodselsDato =  getSqlDate(request.getFodselsDato().getTime());
        java.sql.Date dodsDato = request.getDodsDato() != null ? getSqlDate(request.getDodsDato().getTime()) : null;
        java.sql.Date utvandretDato = request.getUtvandingsDato() != null ? getSqlDate(request.getUtvandingsDato().getTime()) : null;

        penJdbcTemplate.execute(personPreparedStatement, (PreparedStatementCallback<Boolean>) ps -> {
            ps.setString(1, request.getFnr());

            ps.setDate(2, fodselsDato); //No null check, can fail if not present.

            if (dodsDato != null) {
                ps.setDate(3, dodsDato);
            } else {
                ps.setNull(3, Types.DATE);
            }

            if (utvandretDato != null) {
                ps.setDate(4, utvandretDato);
            } else {
                ps.setNull(4, Types.DATE);
            }

            if (request.getBostedsland() != null) {
                ps.setInt(5, 161); //TODO: Need to fetch the acctual bostedsland from T_K_LAND_3_TEGN
            } else {
                ps.setNull(5, Types.VARCHAR);
            }

            ps.setDate(6, getSqlDate(LocalDate.now().toEpochDay()));
            ps.setString(7, "PENSJON-TESTDATA");
            ps.setDate(8, getSqlDate(LocalDate.now().toEpochDay()));
            ps.setString(9, "PENSJON-TESTDATA");
            ps.setInt(10, 1);

            return ps.execute();
        });
    }

    private java.sql.Date getSqlDate(long l) {
        return new java.sql.Date(l);
    }

    /*
     * Operasjonen dekker et ønske fra Dolly om  å vite hvilke miljøer Pensjon er tilgjengelig.
     */
    @RequestMapping(method = RequestMethod.GET, path = "/miljo")
    public ResponseEntity<List<String>> opprettPerson() {
        List<String> miljo = new ArrayList<>();
        miljo.add("q0");
        miljo.add("q1");
        miljo.add("q2");
        miljo.add("q3");
        miljo.add("q4");
        miljo.add("q5");
        miljo.add("q6");
        miljo.add("q8");
        miljo.add("t0");
        miljo.add("t1");
        miljo.add("t2");
        miljo.add("t3");
        miljo.add("t4");
        miljo.add("t5");
        miljo.add("t6");
        miljo.add("t8");
        miljo.add("u12");
        miljo.add("u15");
        miljo.add("u18");
        miljo.add("u8");
        return ResponseEntity.ok(miljo);
    }
}
