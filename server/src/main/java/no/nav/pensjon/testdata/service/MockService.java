package no.nav.pensjon.testdata.service;

import no.nav.pensjon.testdata.repository.OracleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MockService {

    @Autowired
    OracleRepository oracleRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Transactional
    public void iverksett(Long vedtakId) throws IOException, SQLException {
        ClassPathResource resource = new ClassPathResource("/mock/iverksetting.sql");
        Path path = Paths.get(resource.getFile().getPath());
        String allSql = null;
        if (Files.exists(path)) {
            allSql = new String(Files.readAllBytes(path));
        } else {
            throw new FileNotFoundException("Could not find SQL file: " + path);
        }

        String sakId = ((BigDecimal) jdbcTemplate.queryForList("SELECT SAK_ID FROM PEN.T_VEDTAK WHERE VEDTAK_ID = " + vedtakId).get(0).get("SAK_ID")).toString();

        Map<String, String> handlebars = new HashMap<>();
        handlebars.put("VEDTAK", vedtakId.toString());
        handlebars.put("SAK", sakId);
        String newSql = HandlebarTransformer.execute(allSql, handlebars);

        Arrays.stream(newSql.split(";")).forEach(
                sql -> jdbcTemplate.execute(sql)
        );
    }

    @Transactional
    public void attester(Long vedtakId) throws IOException, SQLException {
        String sqlAttestereVedtak = "UPDATE PEN.T_VEDTAK v" +
                " SET v.ANSV_SAKSBH    = 'TESTDATA'," +
                "    v.ATTESTERER     = 'TESTDATA'," +
                "    v.K_VEDTAK_S     = 'ATT'," +
                "    v.DATO_ATTESTERT = CURRENT_DATE," +
                "    v.ENDRET_AV      = 'TESTDATA'," +
                "    v.DATO_ENDRET    = CURRENT_TIMESTAMP" +
                " WHERE v.VEDTAK_ID = " + vedtakId;

        String kravId = ((BigDecimal) jdbcTemplate.queryForList("SELECT KRAVHODE_ID FROM PEN.T_VEDTAK WHERE VEDTAK_ID = " + vedtakId).get(0).get("KRAVHODE_ID")).toString();

        jdbcTemplate.execute(sqlAttestereVedtak);
        List<Map<String, Object>> kravlinjer = jdbcTemplate.queryForList("SELECT KRAVLINJE_ID FROM PEN.T_KRAVLINJE WHERE KRAVHODE_ID = " + kravId);

        kravlinjer.forEach(kravlinje -> {
            String kravlinjeId = ((BigDecimal) kravlinje.get("KRAVLINJE_ID")).toString();
            String nextSequenceValue = ((BigDecimal) jdbcTemplate.queryForList("SELECT S_KRAVLINJE_S.nextval FROM DUAL").get(0).get("NEXTVAL")).toString();

            String insertKravlinjeStatusSql = "INSERT INTO PEN.T_KRAVLINJE_S " +
                    "(KRAVLINJE_S_ID, KRAVLINJE_ID, K_KRAVLINJE_S, DATO_OPPRETTET, OPPRETTET_AV, DATO_ENDRET," +
                    "                               ENDRET_AV, VERSJON)" +
                    "VALUES (" + nextSequenceValue +
                    ", " + kravlinjeId + "," +
                    "       'ATT', CURRENT_TIMESTAMP," +
                    "       'TESTDATA'," +
                    "    CURRENT_TIMESTAMP," +
                    "       'TESTDATA', 0" +
                    ")";

            jdbcTemplate.execute(insertKravlinjeStatusSql);

            String updateKravlinjeSql = "UPDATE PEN.T_KRAVLINJE kl SET kl.KRAVLINJE_S_ID = " + nextSequenceValue +
                    ", ENDRET_AV = 'TESTDATA', DATO_ENDRET = CURRENT_TIMESTAMP WHERE kravlinje_id = " + kravlinjeId;

            String updateKravStatus = "UPDATE PEN.T_KRAVHODE SET K_KRAV_S = 'FERDIG', " +
                    "                  ENDRET_AV = 'TESTDATA'," +
                    "                  DATO_ENDRET = CURRENT_TIMESTAMP " +
                    "WHERE KRAVHODE_ID = " + kravId;

            jdbcTemplate.execute(updateKravlinjeSql);
            jdbcTemplate.execute(updateKravStatus);
        });
    }

    @Transactional
    public void flyttEnhet(Long sakId, Long enhetId) throws IOException, SQLException {
        String flyttEnhetSql = "UPDATE PEN.T_SAK_TILGANG st " +
                "        SET PEN_ORG_ENHET_ID = ( " +
                "                SELECT pOrg.PEN_ORG_ENHET_ID FROM PEN.T_PEN_ORG_ENHET pOrg " +
                "                WHERE pOrg.ORG_ENHET_ID_FK = " + enhetId +
                "                fetch first 1 rows only" +
                "            ), " +
                "            ENDRET_AV = 'TESTDATA'," +
                "            DATO_ENDRET = CURRENT_TIMESTAMP" +
                "    WHERE st.K_TILGANG_T = 'PERM'" +
                "    AND st.DATO_TOM IS NULL" +
                "    AND SAK_ID = " + sakId;
        jdbcTemplate.execute(flyttEnhetSql);
    }
}