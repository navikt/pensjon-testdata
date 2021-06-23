package no.nav.pensjon.testdata.configuration.support;

import no.nav.pensjon.testdata.repository.support.ComponentCode;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Muliggjøre kjøring av applikasjonen uten at alle datakilder er tilgjengelig.
 */
public class JdbcTemplateWrapper {
    private HashMap<ComponentCode, JdbcTemplate> jdbcTemplateMap = new HashMap<>();

    public JdbcTemplateWrapper(JdbcTemplate jdbcTemplatePen, JdbcTemplate jdbcTemplatePopp, JdbcTemplate jdbcTemplateSam) {
        jdbcTemplateMap.put(ComponentCode.PEN, jdbcTemplatePen);
        jdbcTemplateMap.put(ComponentCode.POPP, jdbcTemplatePopp);
        jdbcTemplateMap.put(ComponentCode.SAM, jdbcTemplateSam);
    }


    public void execute(ComponentCode component, String sql) {
        if (jdbcTemplateMap.get(component) != null) {
            jdbcTemplateMap.get(component).execute(sql);
        }
    }

    public String queryForString(ComponentCode component,String sql,  Object[] params) {
        if (jdbcTemplateMap.get(component) != null) {
            return jdbcTemplateMap.get(component).queryForObject(sql, params, (resultSet, i) -> resultSet.getString(1));
        } else {
            throw new NotConnectedToDatabaseException("Not possible when " + component + " is not available");
        }
    }


    public List<Map<String, Object>> queryForList(ComponentCode component, String sql) {
        if (jdbcTemplateMap.get(component) != null) {
            return jdbcTemplateMap.get(component).queryForList(sql);
        } else {
            throw new NotConnectedToDatabaseException("Not possible when " + component + " is not available");
        }
    }

    public <T> List<T>  queryForList(ComponentCode component, String sql,  RowMapper<T> rowMapper) {
        if (jdbcTemplateMap.get(component) != null) {
            return jdbcTemplateMap.get(component).query(sql, rowMapper);
        } else {
            throw new NotConnectedToDatabaseException("Not possible when " + component + " is not available");
        }
    }

    public Object execute(ComponentCode component, String sql, PreparedStatementCallback callback)  {
        if (jdbcTemplateMap.get(component) != null) {
            return jdbcTemplateMap.get(component).execute(sql, callback);
        } else {
            throw new NotConnectedToDatabaseException("Not possible when " + component + " is not available");
        }
    }


}
