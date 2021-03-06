package no.nav.pensjon.testdata.service.support;

public class ChangeStampTransformer {
    public static String execute(String sql) {
        return sql
                .replace("\"OPPRETTET_AV\"\\s=\\s'(.*?)'", "\"OPPRETTET_AV\" = 'PEN-SYNT-TD'")
                .replace("\"ENDRET_AV\"\\s=\\s'(.*?)'", "\"ENDRET_AV\" = 'PEN-SYNT-TD'");
    }
}
