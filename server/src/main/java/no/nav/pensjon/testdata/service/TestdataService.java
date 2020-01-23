package no.nav.pensjon.testdata.service;

import no.nav.pensjon.testdata.repository.FileRepository;
import no.nav.pensjon.testdata.repository.OracleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
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
    public void createTestcase(String testCaseId, Map<String, String> handlebars, boolean opprettPerson) throws IOException, SQLException {
        oracleRepository.alterSession();
        List<String> statements = fileRepository.readSqlStatements("/scenario/" + testCaseId);

        String fnr = handlebars.get("fnr");
        BigDecimal personIdInDatabase = null;
        if (!opprettPerson) {
            personIdInDatabase = (BigDecimal) jdbcTemplate.queryForList("SELECT PERSON_ID FROM PEN.T_PERSON WHERE FNR_FK = '" + fnr + "'").get(0).get("PERSON_ID");
        }
        String personIdInScenario = PersonIdExtractor.execute(statements);
        BigDecimal finalPersonIdInDatabase = personIdInDatabase;

        statements
                .stream()
                .map(statement -> HandlebarTransformer.execute(statement, handlebars))
                .map(ChangeStampTransformer::execute)
                .map(statement -> !opprettPerson && personIdInScenario != null ? statement.replace(personIdInScenario, finalPersonIdInDatabase.toString()) : statement)
                .map(statement -> statement.substring(statement.length()-1, statement.length()).equals(";") ? statement.substring(0, statement.length() -1 ) : statement)
                .filter(sql -> opprettPerson || !sql.contains("\"T_PERSON\""))
                .forEach(statement -> oracleRepository.executeQuery(statement));
    }
}
