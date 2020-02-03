package no.nav.pensjon.testdata.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pensjon.testdata.repository.FileRepository;
import no.nav.pensjon.testdata.repository.OracleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Service
public class TestdataService {

    Logger logger = LoggerFactory.getLogger(TestdataService.class);

    @Autowired
    OracleRepository oracleRepository;
    @Autowired
    FileRepository fileRepository;
    @Qualifier("primaryJdbcTemplate")
    @Autowired
    JdbcTemplate jdbcTemplatePen;

    @Autowired
    @Qualifier("poppJdbcTemplate")
    JdbcTemplate jdbcTemplatePopp;

    @Transactional
    public void createTestcase(String testCaseId, Map<String, String> handlebars) throws IOException {
        oracleRepository.alterSession();
        Scenario scenario = getScenario(testCaseId);
        process(scenario, handlebars,jdbcTemplatePen,scenario.getPenFileSrc(), scenario.getPersonIdPen());
        process(scenario, handlebars,jdbcTemplatePopp,scenario.getPoppFileSrc(), scenario.getPersonIdPopp());
    }

    private Scenario getScenario(String scenarioId) throws IOException {
        ClassPathResource resource = new ClassPathResource("/scenario/");
        ObjectMapper objectMapper = new ObjectMapper();
        for (File file : resource.getFile().listFiles()) {
            if (file.isDirectory() ) {
                Scenario scenario = objectMapper.readValue(Paths.get(file.toString(),"scenario.json").toFile(), Scenario.class);
                if (scenario.getName().equals(scenarioId)) {
                    scenario.setPenFileSrc(Paths.get(file.getPath(), scenario.penFile).toFile());
                    scenario.setPoppFileSrc(Paths.get(file.getPath(), scenario.poppFile).toFile());
                    return scenario;
                }
            }
        }
        throw  new RuntimeException("Could not find scenario!");
    }


    private void process(Scenario scenario, Map<String, String> handlebars, JdbcTemplate jdbcTemplate, File file, String personIdInScenario) throws IOException {
        List<String> statements = fileRepository.readSqlStatements(file, scenario.personIdPen, scenario.getPersonIdPopp());

        String fnr = handlebars.get("fnr");
        BigDecimal personIdInDatabase = null;
        boolean opprettPerson = false;
        if (fnr != null) {
            List<Map<String, Object>> person = jdbcTemplate.queryForList("SELECT PERSON_ID FROM T_PERSON WHERE FNR_FK = '" + fnr + "'");
            opprettPerson = person.isEmpty();

            if (!person.isEmpty()) {
                personIdInDatabase = (BigDecimal) person.get(0).get("PERSON_ID");
            }
        }

        BigDecimal finalPersonIdInDatabase = personIdInDatabase;
        boolean finalOpprettPerson = opprettPerson;
        String finalPersonIdInScenario = personIdInScenario;
        statements
                .stream()
                .map(statement -> statement.trim())
                .map(statement -> HandlebarTransformer.execute(statement, handlebars))
                .map(ChangeStampTransformer::execute)
                .map(statement -> !finalOpprettPerson && finalPersonIdInScenario != null ? statement.replace(finalPersonIdInScenario, finalPersonIdInDatabase.toString()) : statement)
                .map(statement -> statement.substring(statement.length() - 1, statement.length()).equals(";") ? statement.substring(0, statement.length() - 1) : statement)
                .filter(sql -> finalOpprettPerson || !sql.contains("\"T_PERSON\""))
                .filter(sql -> !sql.contains("T_OS_OPPDRLINJE_S"))
                .forEach(statement -> {
                        logger.info(statement);
                        jdbcTemplate.execute(statement);
                });
    }
}
