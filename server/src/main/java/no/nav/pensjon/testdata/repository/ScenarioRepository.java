package no.nav.pensjon.testdata.repository;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import no.nav.pensjon.testdata.configuration.support.JdbcTemplateWrapper;
import no.nav.pensjon.testdata.repository.support.Component;
import no.nav.pensjon.testdata.repository.support.ComponentCode;
import no.nav.pensjon.testdata.repository.support.PathUtil;
import no.nav.pensjon.testdata.repository.support.Person;
import no.nav.pensjon.testdata.repository.support.PrimaryKeySwapper;
import no.nav.pensjon.testdata.repository.support.TestScenario;

@Repository
public class ScenarioRepository {

    @Autowired
    private JdbcTemplateWrapper jdbcTemplateWrapper;

    public TestScenario init(String scenarioId, Map<String, String> handlebars) throws IOException {
        TestScenario testScenario = getTestScenario(scenarioId);
        for (Component component : testScenario.getComponents()) {
            for (Person person : component.getPersoner()) {
                person.init(handlebars, component.getComponent(), jdbcTemplateWrapper);
                if (!person.isFinnesIDatabase()) {
                    throw new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Person i tescenario finnes ikke i " + component.getComponent(), null);
                }
            }
        }
        PrimaryKeySwapper.initializePrimaryKeyRegistry(testScenario.getAllePersonIds());
        return testScenario;
    }

    public TestScenario getTestScenario(String scenarioId) throws IOException {
        for (File file : PathUtil.readPath("scenario/").toFile().listFiles()) {
            if (file.isDirectory()) {
                TestScenario scenario = getObjectMapper().readValue(PathUtil.readPath(file.toString() + "/scenario.json").toFile(), TestScenario.class);
                if (scenario.getName().equals(scenarioId)) {
                    return scenario;
                }
            }
        }
        throw new RuntimeException("Could not find scenario!");
    }

    public void execute(Component component, String sql) {
        jdbcTemplateWrapper.execute(component.getComponent(), sql);
    }

    public Optional<String> insertPenOrgEnhetIfNotExists(String penOrgEnhetId){
        try{
            jdbcTemplateWrapper.queryForString(ComponentCode.PEN,
                "SELECT PEN_ORG_ENHET_ID FROM PEN.T_PEN_ORG_ENHET WHERE PEN_ORG_ENHET_ID = ?", new Object[] {penOrgEnhetId});
        } catch (EmptyResultDataAccessException e){
            String nextSequenceValue = jdbcTemplateWrapper.queryForList(ComponentCode.PEN, "SELECT PEN.S_PEN_ORG_ENHET.nextval FROM DUAL").get(0).get("NEXTVAL").toString();
            jdbcTemplateWrapper.execute(ComponentCode.PEN,
                    "INSERT INTO PEN.T_PEN_ORG_ENHET (PEN_ORG_ENHET_ID, ORG_ENHET_ID_FK, DATO_OPPRETTET, OPPRETTET_AV, DATO_ENDRET, ENDRET_AV, VERSJON)"
                    + " VALUES (" + nextSequenceValue + ", 'XXXXXX', CURRENT_TIMESTAMP, 'TESTDATA', CURRENT_TIMESTAMP, 'TESTDATA', 0)");
            return Optional.of(nextSequenceValue);
        }
        return Optional.empty();
    }

    public ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        return objectMapper;
    }

    public void setJdbcTemplateWrapper(JdbcTemplateWrapper jdbcTemplateWrapper) {
        this.jdbcTemplateWrapper = jdbcTemplateWrapper;
    }
}
