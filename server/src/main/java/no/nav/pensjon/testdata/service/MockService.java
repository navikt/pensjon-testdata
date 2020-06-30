package no.nav.pensjon.testdata.service;

import no.nav.pensjon.testdata.configuration.support.JdbcTemplateWrapper;
import no.nav.pensjon.testdata.repository.OracleRepository;
import no.nav.pensjon.testdata.repository.support.ComponentCode;
import no.nav.pensjon.testdata.repository.support.PathUtil;
import no.nav.pensjon.testdata.service.support.HandlebarTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MockService {

    private Logger logger = LoggerFactory.getLogger(MockService.class);

    @Autowired
    private OracleRepository oracleRepository;

    @Autowired
    private JdbcTemplateWrapper jdbcTemplateWrapper;

    @Transactional
    public void iverksett(Long vedtakId) throws IOException, SQLException {
        Path path = PathUtil.readPath("mock/iverksetting.sql");
        String allSql = null;
        if (Files.exists(path)) {
            allSql = new String(Files.readAllBytes(path));
        } else {
            throw new FileNotFoundException("Could not find SQL file: " + path);
        }

        String sakId = ((BigDecimal) jdbcTemplateWrapper.queryForList(ComponentCode.PEN, "SELECT SAK_ID FROM PEN.T_VEDTAK WHERE VEDTAK_ID = " + vedtakId).get(0).get("SAK_ID")).toString();

        Map<String, String> handlebars = new HashMap<>();
        handlebars.put("VEDTAK", vedtakId.toString());
        handlebars.put("SAK", sakId);
        String newSql = HandlebarTransformer.execute(allSql, handlebars);

        Arrays.stream(newSql.split(";")).forEach(
                sql -> jdbcTemplateWrapper.execute(ComponentCode.PEN, sql)
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

        String kravId = ((BigDecimal) jdbcTemplateWrapper.queryForList(ComponentCode.PEN, "SELECT KRAVHODE_ID FROM PEN.T_VEDTAK WHERE VEDTAK_ID = " + vedtakId).get(0).get("KRAVHODE_ID")).toString();

        jdbcTemplateWrapper.execute(ComponentCode.PEN, sqlAttestereVedtak);
        List<Map<String, Object>> kravlinjer = jdbcTemplateWrapper.queryForList(ComponentCode.PEN, "SELECT KRAVLINJE_ID FROM PEN.T_KRAVLINJE WHERE KRAVHODE_ID = " + kravId);

        kravlinjer.forEach(kravlinje -> {
            String kravlinjeId = ((BigDecimal) kravlinje.get("KRAVLINJE_ID")).toString();
            String nextSequenceValue = ((BigDecimal) jdbcTemplateWrapper.queryForList(ComponentCode.PEN, "SELECT PEN.S_KRAVLINJE_S.nextval FROM DUAL").get(0).get("NEXTVAL")).toString();

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

            jdbcTemplateWrapper.execute(ComponentCode.PEN, insertKravlinjeStatusSql);

            String updateKravlinjeSql = "UPDATE PEN.T_KRAVLINJE kl SET kl.KRAVLINJE_S_ID = " + nextSequenceValue +
                    ", ENDRET_AV = 'TESTDATA', DATO_ENDRET = CURRENT_TIMESTAMP WHERE kravlinje_id = " + kravlinjeId;

            String updateKravStatus = "UPDATE PEN.T_KRAVHODE SET K_KRAV_S = 'FERDIG', " +
                    "                  ENDRET_AV = 'TESTDATA'," +
                    "                  DATO_ENDRET = CURRENT_TIMESTAMP " +
                    "WHERE KRAVHODE_ID = " + kravId;

            jdbcTemplateWrapper.execute(ComponentCode.PEN, updateKravlinjeSql);
            jdbcTemplateWrapper.execute(ComponentCode.PEN, updateKravStatus);
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
        jdbcTemplateWrapper.execute(ComponentCode.PEN, flyttEnhetSql);
    }

    public void opprettPenPerson(String fnr) {
        //If FNR what so simple ;)
        String year = 19 + fnr.substring(4,6);
        String month = fnr.substring(2,4);
        String day = fnr.substring(0,2);

        String birthDay = year + "-" + month + "-" + day;

        String sql = "insert into " +
                "PEN.T_PERSON(" +
                "FNR_FK," +
                "DATO_FODSEL," +
                "DATO_OPPRETTET," +
                "DATO_DOD," +
                "DATO_UTVANDRET," +
                "OPPRETTET_AV," +
                "DATO_ENDRET," +
                "ENDRET_AV," +
                "VERSJON," +
                "BOSTEDSLAND) " +
                "values " +
                "('"+
                fnr + "'" +
                ",TO_DATE('"+birthDay+" 00:00:00', 'YYYY-MM-DD HH24:MI:SS')," +
                "CURRENT_TIMESTAMP," +
                "NULL," +
                "NULL," +
                "'MOOG'," +
                "CURRENT_TIMESTAMP," +
                "'MOOG'," +
                "'0'," +
                "NULL)";
        jdbcTemplateWrapper.execute(ComponentCode.PEN, sql);
    }

    public void opprettPoppPerson(String fnr) {
        String sql = "insert into " +
                "POPP.T_PERSON(" +
                "FNR_FK," +
                "DATO_OPPRETTET," +
                "OPPRETTET_AV," +
                "DATO_ENDRET," +
                "ENDRET_AV," +
                "VERSJON)" +
                "values " +
                "('"+
                fnr + "'," +
                "CURRENT_TIMESTAMP," +
                "'MOOG'," +
                "CURRENT_TIMESTAMP," +
                "'MOOG'," +
                "'0')";
        jdbcTemplateWrapper.execute(ComponentCode.POPP, sql);
    }
}