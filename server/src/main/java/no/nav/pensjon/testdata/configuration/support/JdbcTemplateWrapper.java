package no.nav.pensjon.testdata.configuration.support;

import no.nav.pensjon.testdata.controller.OpptjeningController;
import no.nav.pensjon.testdata.repository.support.ComponentCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static no.nav.pensjon.testdata.repository.support.ComponentCode.*;

/*
 * Muliggjøre kjøring av applikasjonen uten at alle datakilder er tilgjengelig.
 */
public class JdbcTemplateWrapper {
    private static final Logger LOG = LoggerFactory.getLogger(OpptjeningController.class);
    private final HashMap<ComponentCode, JdbcTemplate> jdbcTemplateMap = new HashMap<>();

    public JdbcTemplateWrapper(JdbcTemplate jdbcTemplatePen, JdbcTemplate jdbcTemplatePopp, JdbcTemplate jdbcTemplateSam) {
        jdbcTemplateMap.put(PEN, jdbcTemplatePen);
        jdbcTemplateMap.put(POPP, jdbcTemplatePopp);
        jdbcTemplateMap.put(SAM, jdbcTemplateSam);
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

    public boolean pingPEN(){
        try{
            return Optional.ofNullable(jdbcTemplateMap.get(PEN)).stream().peek(pen -> pen.execute("SELECT 1 FROM PEN.T_PERSON")).count() > 0;
        }
        catch(DataAccessException e){
            LOG.error("Could not ping PEN", e);
        }
        return false;
    }

    public boolean pingPOPP(){
        try{
            return Optional.ofNullable(jdbcTemplateMap.get(POPP)).stream().peek(pen -> pen.execute("SELECT 1 FROM POPP.T_PERSON")).count() > 0;
        }
        catch(DataAccessException e){
            LOG.error("Could not ping POPP", e);
        }
        return false;
    }

    public boolean pingSAM(){
        try{
            return Optional.ofNullable(jdbcTemplateMap.get(SAM)).stream().peek(pen -> pen.execute("SELECT 1 FROM SAM.T_PERSON")).count() > 0;
        }
        catch(DataAccessException e){
            LOG.error("Could not ping SAM", e);
        }
        return false;
    }
}
