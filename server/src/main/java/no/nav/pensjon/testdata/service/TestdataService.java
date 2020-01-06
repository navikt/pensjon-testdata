package no.nav.pensjon.testdata.service;

import no.nav.pensjon.testdata.repository.FileRepository;
import no.nav.pensjon.testdata.repository.OracleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
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

    public void createTestcase(String testCaseId, Map<String, String> handlebars, String server, String database, String username, String password, boolean opprettPerson) throws IOException, SQLException {
        List<String> statements = fileRepository.readSqlStatements("/scenario/" + testCaseId);

        JdbcTemplate jdbcTemplate = oracleRepository.createJdbcTemplate(server, database, username, password);
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(jdbcTemplate.getDataSource());
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        String fnr = handlebars.get("fnr");
        BigDecimal personIdInDatabase = null;
        if (!opprettPerson) {
            personIdInDatabase = (BigDecimal) jdbcTemplate.queryForList("SELECT PERSON_ID FROM PEN.T_PERSON WHERE FNR_FK = '" + fnr + "'").get(0).get("PERSON_ID");
        }
        String personIdInScenario = PersonIdExtractor.execute(statements);
        BigDecimal finalPersonIdInDatabase = personIdInDatabase;

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {

                statements
                        .stream()
                        .map(statement -> HandlebarTransformer.execute(statement, handlebars))
                        .map(ChangeStampTransformer::execute)
                        .map(statement -> !opprettPerson ? statement.replace(personIdInScenario, finalPersonIdInDatabase.toString()) : statement)
                        .filter(sql -> opprettPerson || !sql.contains("\"T_PERSON\""))
                        .forEach(statement -> oracleRepository.executeQuery(statement, jdbcTemplate));
            }
        });
        jdbcTemplate.getDataSource().getConnection().close();
    }
}
