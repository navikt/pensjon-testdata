package no.nav.pensjon.testdata.repository;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pensjon.testdata.configuration.support.JdbcTemplateWrapper;
import no.nav.pensjon.testdata.repository.support.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class ScenarioRepository {

    @Autowired
    private JdbcTemplateWrapper jdbcTemplateWrapper;
    private static final Logger logger = LoggerFactory.getLogger(ScenarioRepository.class);
    private Map<String, TestScenario> tilgjengeligeTestScenarioer;
    private final ObjectMapper objectMapper;

    {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    }

    public TestScenario init(String scenarioId, Map<String, String> handlebars) throws IOException {
        TestScenario testScenario = obtainScenarioCopy(scenarioId);
        for (Component component : testScenario.getComponents()) {
            for (Person person : component.getPersoner()) {
                person.init(handlebars, component.getComponent(), jdbcTemplateWrapper);
                if (!person.isFinnesIDatabase()) {
                    throw new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Person i testcenario finnes ikke i " + component.getComponent(), null);
                }
            }
        }
        PrimaryKeySwapper.initializePrimaryKeyRegistry(TestScenarioUtil.getAllePersonIds(testScenario));
        return testScenario;
    }

    public List<TestScenario> getAllTestScenarios() {
        return new ArrayList<>(tilgjengeligeTestScenarioer.values());
    }

    @PostConstruct
    private void initTestScenarios(){
        tilgjengeligeTestScenarioer = Arrays.stream(Objects.requireNonNull(PathUtil.readPath("scenario/").toFile().listFiles()))
                .filter(File::isDirectory)
                .map(f -> f.toString() + "/scenario.json")
                .map(f -> {
                    try {
                        return objectMapper.readValue(PathUtil.readPath(f).toFile(), TestScenario.class);
                    } catch (IOException e) {
                        logger.error("Could not read scenario at path: " + f, e);
                        throw new UncheckedIOException("Could not read scenario at path: " + f, e);
                    }
                }).collect(Collectors.toConcurrentMap(TestScenario::getScenarioId, Function.identity()));
        logger.info("tilgjengelige scenarioer: " + tilgjengeligeTestScenarioer.keySet());
    }

    public TestScenario obtainScenarioCopy(String scenarioName) throws IOException {
        return Optional.ofNullable(tilgjengeligeTestScenarioer.get(scenarioName)).orElseThrow(IOException::new);
/*        return tilgjengeligeTestScenarioer.values()
                .stream()
                .filter(t -> t.getName().equalsIgnoreCase(scenarioName))
                .findFirst()
                .map(t -> {
                    try {
                        return objectMapper.readValue(objectMapper.writeValueAsString(t), TestScenario.class);
                    } catch (JsonProcessingException e) {
                        logger.error(e.getMessage(), e);
                        return t; //da vil fnr-validering bomme
                    }
                })
                .orElseThrow(IOException::new);*/
    }

    public void execute(Component component, String sql) {
        jdbcTemplateWrapper.execute(component.getComponent(), sql);
    }

    public Optional<String> insertPenOrgEnhetIfNotExists(String penOrgEnhetId){
        try{
            String key = jdbcTemplateWrapper.queryForString(ComponentCode.PEN,
                "SELECT PEN_ORG_ENHET_ID FROM PEN.T_PEN_ORG_ENHET WHERE PEN_ORG_ENHET_ID = ?", new Object[] {penOrgEnhetId});
            return Optional.ofNullable(key);
        } catch (EmptyResultDataAccessException e){
            String nextSequenceValue = jdbcTemplateWrapper.queryForList(ComponentCode.PEN, "SELECT PEN.S_PEN_ORG_ENHET.nextval FROM DUAL").get(0).get("NEXTVAL").toString();
            jdbcTemplateWrapper.execute(ComponentCode.PEN,
                    "INSERT INTO PEN.T_PEN_ORG_ENHET (PEN_ORG_ENHET_ID, ORG_ENHET_ID_FK, DATO_OPPRETTET, OPPRETTET_AV, DATO_ENDRET, ENDRET_AV, VERSJON)"
                    + " VALUES (" + nextSequenceValue + ", '4415', CURRENT_TIMESTAMP, 'TESTDATA', CURRENT_TIMESTAMP, 'TESTDATA', 0)");
            return Optional.ofNullable(nextSequenceValue);
        }
    }

    public void setJdbcTemplateWrapper(JdbcTemplateWrapper jdbcTemplateWrapper) {
        this.jdbcTemplateWrapper = jdbcTemplateWrapper;
    }
}
