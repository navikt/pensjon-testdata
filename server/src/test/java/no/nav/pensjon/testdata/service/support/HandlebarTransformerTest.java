package no.nav.pensjon.testdata.service.support;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class HandlebarTransformerTest {

    @Test
    void executeHappyCase() {
        final String newValue = "12345";
        String result = HandlebarTransformer.execute("SELECT * FROM TABLE WHERE ID='{id}'", Collections.singletonMap("id", newValue));
        Assert.assertFalse(result.contains("{"));
        Assert.assertFalse(result.contains("}"));
        Assert.assertTrue(result.contains(newValue));
    }

    @Test
    void executeWithInputtype() {
        final String newValue = "12345";
        String result = HandlebarTransformer.execute("SELECT * FROM TABLE WHERE ID='{id|number}'", Collections.singletonMap("id", newValue));
        Assert.assertFalse(result.contains("{"));
        Assert.assertFalse(result.contains("}"));
        Assert.assertTrue(result.contains(newValue));
    }

    @Test
    void executeWithInputtypeAndValidators() {
        final String newValue = "12345";
        String result = HandlebarTransformer.execute("SELECT * FROM TABLE WHERE ID='{id|number|validator}'", Collections.singletonMap("id", newValue));
        Assert.assertFalse(result.contains("{"));
        Assert.assertFalse(result.contains("}"));
        Assert.assertTrue(result.contains(newValue));
    }

    @Test
    void executeWithseveralHandlbars() {
        Map<String, String> handlebars = new HashMap<>();
        handlebars.put("id", "12345");
        handlebars.put("anotherid", "98765");
        String result = HandlebarTransformer.execute("SELECT * FROM TABLE WHERE ID='{id|number|validator}' AND anotherid ='{anotherid|number|validator}' ", handlebars);
        Assert.assertFalse(result.contains("{"));
        Assert.assertFalse(result.contains("}"));
        Assert.assertTrue(result.contains("12345"));
        Assert.assertTrue(result.contains("98765"));
    }

    @Test
    void executeWithseveralSameHandlbars() {
        Map<String, String> handlebars = new HashMap<>();
        handlebars.put("id", "12345");
        String result = HandlebarTransformer.execute("SELECT * FROM TABLE WHERE ID='{id|number|validator}' "
                + "AND second_id ='{id|number|validator}' "
                + "AND third_id ='{id|number|validator}' ", handlebars);
        Assert.assertFalse(result.contains("{"));
        Assert.assertFalse(result.contains("}"));
        Assert.assertTrue(result.contains("12345"));
    }

    @Test
    void executeWithNoHandlebars() {
        String sql = "SELECT WHATEVER FROM WHEREVER WHERE ID={id|number}";
        String result = HandlebarTransformer.execute(sql, Collections.emptyMap());
        Assert.assertEquals(result,sql);
    }

    @Test
    void executeWithSeveralHandlebars(){
        String sql = "UPDATE PEN.T_VEDTAK v\n" +
                "SET K_VEDTAK_S          = 'IVERKS',\n" +
                "    v.DATO_TILVERKSETT  = CURRENT_DATE,\n" +
                "    v.DATO_IVERKSATT    = CURRENT_DATE,\n" +
                "    v.DATO_SENDT_SAMORD = CURRENT_DATE,\n" +
                "    v.DATO_SAMORDNET    = CURRENT_DATE,\n" +
                "    v.ENDRET_AV         = 'TESTDATA',\n" +
                "    V.DATO_ENDRET       = CURRENT_TIMESTAMP\n" +
                "WHERE v.vedtak_id = {VEDTAK};\n" +
                "\n" +
                "update pen.t_vedtak v\n" +
                "set v.dato_lopende_fom = v.dato_virk_fom,\n" +
                "    ENDRET_AV          = 'TESTDATA',\n" +
                "    DATO_ENDRET        = CURRENT_TIMESTAMP\n" +
                "where not exists(select 1\n" +
                "                 from pen.t_vedtak v2\n" +
                "                 where v2.sak_id = v.sak_id\n" +
                "                   and v2.vedtak_id > v.vedtak_id\n" +
                "                   and v2.dato_iverksatt is not null\n" +
                "                   and v2.dato_virk_fom <= v.dato_virk_fom)\n" +
                "  AND v.SAK_ID = {SAK}\n" +
                "  ;\n" +
                "\n" +
                "update pen.t_vedtak v\n" +
                "set v.dato_lopende_tom = (select min(v2.dato_lopende_fom) - 1\n" +
                "                          from pen.t_vedtak v2\n" +
                "                          where v2.sak_id = v.sak_id\n" +
                "                            and v2.vedtak_id > v.vedtak_id\n" +
                "                            and v2.dato_lopende_fom is not null),\n" +
                "    ENDRET_AV          = 'TESTDATA',\n" +
                "    DATO_ENDRET        = CURRENT_TIMESTAMP\n" +
                "where v.dato_lopende_fom is not null\n" +
                "  AND v.SAK_ID = {SAK};\n" +
                "\n" +
                "INSERT INTO PEN.T_OS_KVITTERING (VEDTAK_ID, MELDING_KODE, BESK_MELDING, ALVORLIGHETSGRAD, DATO_OPPRETTET, OPPRETTET_AV,\n" +
                "                                 DATO_ENDRET, ENDRET_AV, VERSJON, OS_TRANSAKSJON_ID)\n" +
                "VALUES ({VEDTAK}, null, null,\n" +
                "       '00', CURRENT_TIMESTAMP,\n" +
                "       'TESTDATA', CURRENT_TIMESTAMP,\n" +
                "       'TESTDATA', 0, null\n" +
                ");\n" +
                "\n" +
                "UPDATE PEN.T_SAK s\n" +
                "set K_SAK_S   = 'LOPENDE',\n" +
                "    ENDRET_AV = 'TESTDATA',\n" +
                "    DATO_ENDRET = CURRENT_TIMESTAMP\n" +
                "WHERE SAK_ID = {SAK}\n" +
                "    AND NOT EXISTS (\n" +
                "    SELECT 1\n" +
                "    FROM PEN.T_KRAVHODE h\n" +
                "    WHERE h.SAK_ID = s.SAK_ID\n" +
                "    AND h.K_KRAV_S not in ('FERDIG','AVBRUTT')\n" +
                "    );";
        Map<String,String> handlebars = new HashMap<>();
        handlebars.put("SAK", "12345");
        handlebars.put("VEDTAK", "54321");
        String result = HandlebarTransformer.execute(sql, handlebars);
        Assert.assertFalse(result.contains("{"));
        Assert.assertFalse(result.contains("}"));
    }
}