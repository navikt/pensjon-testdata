package no.nav.pensjon.testdata.repository;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pensjon.testdata.configuration.support.JdbcTemplateWrapper;
import no.nav.pensjon.testdata.repository.support.Component;
import no.nav.pensjon.testdata.repository.support.Person;
import no.nav.pensjon.testdata.repository.support.PrimaryKeySwapper;
import no.nav.pensjon.testdata.repository.support.TestScenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

@Repository
public class ScenarioRepository {

    Logger logger = LoggerFactory.getLogger(ScenarioRepository.class);

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
        PrimaryKeySwapper.initializePrimaryKeyRegistry(testScenario.getAllePersoner());
        return testScenario;
    }

    public TestScenario getTestScenario(String scenarioId) throws IOException {
        ClassPathResource resource = new ClassPathResource("/scenario/");

        for (File file : resource.getFile().listFiles()) {
            if (file.isDirectory()) {
                TestScenario scenario = getObjectMapper().readValue(Paths.get(file.toString(), "scenario.json").toFile(), TestScenario.class);
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

    public ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        return objectMapper;
    }
}
