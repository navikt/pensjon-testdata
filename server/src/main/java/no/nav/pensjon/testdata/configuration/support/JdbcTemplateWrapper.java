package no.nav.pensjon.testdata.configuration.support;

import no.nav.pensjon.testdata.repository.support.Component;
import no.nav.pensjon.testdata.repository.support.ComponentCode;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Muliggjøre kjøring av applikasjonen uten at alle datakilder er tilgjengelig.
 */
public class JdbcTemplateWrapper {

    private JdbcTemplate jdbcTemplatePen;

    private JdbcTemplate jdbcTemplatePopp;

    private JdbcTemplate jdbcTemplateSam;

    private HashMap<ComponentCode, JdbcTemplate> jdbcTemplateMap = new HashMap<>();

    public JdbcTemplateWrapper(JdbcTemplate jdbcTemplatePen, JdbcTemplate jdbcTemplatePopp, JdbcTemplate jdbcTemplateSam) {
        this.jdbcTemplatePen = jdbcTemplatePen;
        this.jdbcTemplatePopp = jdbcTemplatePopp;
        this.jdbcTemplateSam = jdbcTemplateSam;

        jdbcTemplateMap.put(ComponentCode.PEN, jdbcTemplatePen);
        jdbcTemplateMap.put(ComponentCode.POPP, jdbcTemplatePopp);
        jdbcTemplateMap.put(ComponentCode.SAM, jdbcTemplateSam);
    }


    public void execute(ComponentCode component, String sql) {
        if (jdbcTemplateMap.get(component) != null) {
            jdbcTemplateMap.get(component).execute(sql);
        }
    }

    public List<Map<String, Object>> queryForList(ComponentCode component, String sql) {
        if (jdbcTemplateMap.get(component) != null) {
            return jdbcTemplateMap.get(component).queryForList(sql);
        } else {
            throw new RuntimeException("Not possible when " + component + " is not available");
        }
    }

    public List<Map<String, Object>> queryForList(Component component, String sql, PreparedStatementCallback callback)  {
        if (jdbcTemplateMap.get(component) != null) {
            return jdbcTemplateMap.get(component).queryForList(sql, callback);
        } else {
            throw new RuntimeException("Not possible when " + component + " is not available");
        }
    }

    public Object execute(ComponentCode component, String sql, PreparedStatementCallback callback)  {
        if (jdbcTemplateMap.get(component) != null) {
            return jdbcTemplateMap.get(component).execute(sql, callback);
        } else {
            throw new RuntimeException("Not possible when " + component + " is not available");
        }
    }


}
