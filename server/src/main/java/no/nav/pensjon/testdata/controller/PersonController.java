package no.nav.pensjon.testdata.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import no.nav.pensjon.testdata.configuration.support.JdbcTemplateWrapper;
import no.nav.pensjon.testdata.configuration.support.NotConnectedToDatabaseException;
import no.nav.pensjon.testdata.consumer.opptjening.OpptjeningConsumerBean;
import no.nav.pensjon.testdata.consumer.usertoken.HentUserTokenBean;
import no.nav.pensjon.testdata.controller.support.OpprettPersonRequest;
import no.nav.pensjon.testdata.repository.OracleRepository;
import no.nav.pensjon.testdata.repository.support.ComponentCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

@RestController
@Api(tags = {"Person"})
@SwaggerDefinition(tags = {
        @Tag(name = "", description = "Endepunkter for person")
})
@RequestMapping("/api/person")
public class PersonController {

    Logger logger = LoggerFactory.getLogger(PersonController.class);

    @Autowired
    private OpptjeningConsumerBean opptjeningConsumerBean;

    @Autowired
    private JdbcTemplateWrapper jdbcTemplateWrapper;

    @Autowired
    private OracleRepository oracleRepository;

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    HentUserTokenBean hentUserTokenBean;

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

    @PostMapping
    @Transactional
    @ApiOperation(value = "Oppretter personer innenfor pensjonsområdet (PEN, POPP og SAM)")
    public ResponseEntity<HttpStatus> opprettPerson(
            @RequestHeader("Nav-Call-Id") String callId,
            @RequestHeader("Nav-Consumer-Id") String consumerId,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody OpprettPersonRequest body) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String fnr = body.getFnr().substring(0, 4);
        logger.info("Oppretter person:  fnr:" + fnr + "*******" + " fodselsdato: " + df.format(body.getFodselsDato()));

        oracleRepository.alterSession();

        try {
            try {
                createPenPerson(body);
            } catch (DuplicateKeyException s) {
                logger.warn("PEN: tried to insert a duplicate person with fnr: " + fnr + ", " + s.getMessage(), s);
            }
            catch (NotConnectedToDatabaseException e){
                logger.warn("Pensjon-testdata is not connected to PEN");
            }
            try {
                createSamPerson(body);
            } catch (DuplicateKeyException s) {
                logger.warn("SAM: tried to insert a duplicate person with fnr: " + fnr + ", " + s.getMessage(), s);
            }
            catch (NotConnectedToDatabaseException e){
                logger.warn("Pensjon-testdata is not connected to SAM");
            }
            HttpHeaders httpHeaders = createHttpHeaders(callId, consumerId, token, body.getFnr());
            try {
                opptjeningConsumerBean.lagrePerson(httpHeaders);
            } catch (DuplicateKeyException s) {
                logger.warn("POPP: tried to insert a duplicate person with fnr: " + fnr + ", " + s.getMessage(), s);
            }
            catch (NotConnectedToDatabaseException e){
                logger.warn("Pensjon-testdata is not connected to POPP");
            }
            dollyLagrePersonCounter.increment();
        } catch (Exception e) {
            dollyLagrePersonFailedCounter.increment();
            logger.error("Failed to create person with msg: " + e.getMessage(), e);
            throw e;
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    private HttpHeaders createHttpHeaders(String callId, String consumerId, String token, String fnr) {
        String useToken = token != null ? token : "Bearer " + hentUserTokenBean.fetch().getAccessToken();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("Authorization", useToken);
        httpHeaders.add("Nav-Call-Id", callId);
        httpHeaders.add("Nav-Consumer-Id", consumerId);
        httpHeaders.add("fnr", fnr);
        return httpHeaders;
    }

    private void createSamPerson(OpprettPersonRequest request) {
        if (!brukerFinnes(ComponentCode.SAM, request.getFnr())) {
            String personPreparedStatement = "INSERT INTO SAM.T_PERSON " +
                    "(FNR_FK, " +
                    "DATO_OPPRETTET, " +
                    "OPPRETTET_AV, " +
                    "DATO_ENDRET, " +
                    "ENDRET_AV, " +
                    "VERSJON) " +
                    "VALUES (?,?,?,?,?,?)";

            jdbcTemplateWrapper.execute(ComponentCode.SAM, personPreparedStatement, (PreparedStatementCallback<Boolean>) preparedStatement -> {
                preparedStatement.setString(1, request.getFnr());
                preparedStatement.setDate(2, getSqlDate(LocalDate.now()));
                preparedStatement.setString(3, "PENSJON-TESTDATA");
                preparedStatement.setDate(4, getSqlDate(LocalDate.now()));
                preparedStatement.setString(5, "PENSJON-TESTDATA");
                preparedStatement.setInt(6, 0);
                return preparedStatement.execute();
            });
        }
    }

    private void createPenPerson(OpprettPersonRequest request) {
        if (!brukerFinnes(ComponentCode.PEN, request.getFnr())) {
            String personPreparedStatement = "INSERT INTO PEN.T_PERSON " +
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

            java.sql.Date fodselsDato = getSqlDate(request.getFodselsDato());
            java.sql.Date dodsDato = request.getDodsDato() != null ? getSqlDate(request.getDodsDato()) : null;
            java.sql.Date utvandretDato = request.getUtvandringsDato() != null ? getSqlDate(request.getUtvandringsDato()) : null;
            int landKode = fetchLandKode(request.getBostedsland());

            jdbcTemplateWrapper.execute(ComponentCode.PEN, personPreparedStatement, (PreparedStatementCallback<Boolean>) ps -> {
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
                    ps.setInt(5, landKode);
                } else {
                    ps.setNull(5, Types.VARCHAR);
                }

                ps.setDate(6, getSqlDate(LocalDate.now()));
                ps.setString(7, "PENSJON-TESTDATA");
                ps.setDate(8, getSqlDate(LocalDate.now()));
                ps.setString(9, "PENSJON-TESTDATA");
                ps.setInt(10, 1);

                return ps.execute();
            });
        }
    }

    private int fetchLandKode(String bostedsland) {
        return Optional.ofNullable(bostedsland)
                .filter(StringUtils::isNotBlank)
                .map(b -> jdbcTemplateWrapper.queryForList(ComponentCode.PEN, "SELECT K_LAND_3_TEGN_ID FROM T_K_LAND_3_TEGN where land_3_tegn = '" + bostedsland + "'"))
                .filter(list -> !list.isEmpty())
                .map(v -> v.get(0).get("K_LAND_3_TEGN_ID"))
                .map(v -> (BigDecimal) v)
                .map(BigDecimal::intValue)
                .orElse(161);
    }

    private Date getSqlDate(java.util.Date date) {
        return Date.valueOf(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }

    private Date getSqlDate(LocalDate date) {
        return Date.valueOf(date);
    }

    private boolean brukerFinnes(ComponentCode component, String fnr) {
        return jdbcTemplateWrapper.queryForList(component, "SELECT * FROM T_PERSON where fnr_fk = '" + fnr + "'").size() > 0;
    }

}
