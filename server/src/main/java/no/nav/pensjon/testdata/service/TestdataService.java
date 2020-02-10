package no.nav.pensjon.testdata.service;

import no.nav.pensjon.testdata.repository.FileRepository;
import no.nav.pensjon.testdata.repository.OracleRepository;
import no.nav.pensjon.testdata.repository.ScenarioRepository;
import no.nav.pensjon.testdata.repository.support.Scenario;
import no.nav.pensjon.testdata.service.support.ChangeStampTransformer;

import no.nav.pensjon.testdata.service.support.HandlebarTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class TestdataService {

    Logger logger = LoggerFactory.getLogger(TestdataService.class);

    @Autowired
    OracleRepository oracleRepository;
    @Autowired
    FileRepository fileRepository;
    @Autowired
    ScenarioRepository scenarioRepository;
    @Autowired
    @Qualifier("penJdbcTemplate")
    JdbcTemplate jdbcTemplatePen;
    @Autowired
    @Qualifier("poppJdbcTemplate")
    JdbcTemplate jdbcTemplatePopp;

    @Transactional
    public void createTestcase(String testCaseId, Map<String, String> handlebars) throws IOException {
        oracleRepository.alterSession();
        Scenario scenario = scenarioRepository.getScenario(testCaseId);

        process(scenario, handlebars,jdbcTemplatePen,scenario.getPenFileSrc(), scenario.getPersonIdPen());
        process(scenario, handlebars,jdbcTemplatePopp,scenario.getPoppFileSrc(), scenario.getPersonIdPopp());
    }

    private void process(Scenario scenario, Map<String, String> handlebars, JdbcTemplate jdbcTemplate, File file, String personIdInScenario) throws IOException {
        List<String> statements = fileRepository.readSqlFile(file, scenario.getPersonIdPen(), scenario.getPersonIdPopp());

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
                .filter(statement -> statement.length() > 0)
                .map(statement -> HandlebarTransformer.execute(statement, handlebars))
                .map(ChangeStampTransformer::execute)
                .map(statement -> !finalOpprettPerson && finalPersonIdInScenario != null ? statement.replace(finalPersonIdInScenario, finalPersonIdInDatabase.toString()) : statement)
                .map(statement -> statement.substring(statement.length() - 1, statement.length()).equals(";") ? statement.substring(0, statement.length() - 1) : statement)
                .filter(sql -> finalOpprettPerson || !sql.contains("\"T_PERSON\""))
                .filter(this::removeOsOppdragslinjeStatus)
                .forEach(statement -> {
                        logger.info(statement);
                        jdbcTemplate.execute(statement);
                });
    }

    /*
     * Ser bort ifra SQL relatert til T_OS_OPPDRLINJE_S, i et forsøk på å fjerne avhengigheter mot OS.
     * Tabellen gir PEN et bilde av hva som er kommunisert til OS tidligere.
     *
     * For syntetiske testdata har ikke noe blitt kommunisert til OS, og det kan derfor gi mening å ikke ta med dette innholdet.
     */
    private boolean removeOsOppdragslinjeStatus(String sql) {
        return !sql.contains("T_OS_OPPDRLINJE_S");
    }
}
