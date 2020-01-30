package no.nav.pensjon.testdata.service;

import no.nav.pensjon.testdata.repository.FileRepository;
import no.nav.pensjon.testdata.repository.OracleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class TestdataService {


    @Autowired
    OracleRepository oracleRepository;
    @Autowired
    FileRepository fileRepository;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Transactional
    public void createTestcase(String testCaseId, Map<String, String> handlebars) throws IOException {
        oracleRepository.alterSession();
        List<String> statements = fileRepository.readSqlStatements("/scenario/" + testCaseId);

        String fnr = handlebars.get("fnr");
        BigDecimal personIdInDatabase = null;
        String personIdInScenario = null;
        boolean opprettPerson = false;
        if (fnr != null) {
            List<Map<String, Object>> person = jdbcTemplate.queryForList("SELECT PERSON_ID FROM PEN.T_PERSON WHERE FNR_FK = '" + fnr + "'");
            opprettPerson = person.isEmpty();

            if (!person.isEmpty()) {
                personIdInDatabase = (BigDecimal) person.get(0).get("PERSON_ID");
            }
             personIdInScenario = PersonIdExtractor.execute(statements);
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
                .forEach(statement -> oracleRepository.executeQuery(statement));
    }
}
