package no.nav.pensjon.testdata.repository;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pensjon.testdata.controller.NonWhitelistedDatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Repository
public class OracleRepository {
    private final Logger logger = LoggerFactory.getLogger(OracleRepository.class);

    @Autowired
    FileRepository fileRepository;


    public void executeQuery(String query, JdbcTemplate jdbcTemplate) {
        logger.info(query);
        jdbcTemplate.execute(query);
    }

    public void clearDatabase(String server, String database, String username, String password) throws IOException, NonWhitelistedDatabaseException, SQLException {
        ObjectMapper mapper = new ObjectMapper();
        ClassPathResource resource = new ClassPathResource("clear-database-whiteliste.json");
        List<String> databaseWhiteList = mapper.readValue(resource.getFile(), new TypeReference<List<String>>() {
        });

        if (databaseWhiteList.contains(server)) {
            logger.info("Removing all data from database");
            JdbcTemplate jdbcTemplate = createJdbcTemplate(server,database,username,password);
            DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(jdbcTemplate.getDataSource());
            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

            List<String> sql = fileRepository.readSqlStatements("/unload");
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                    sql.forEach(query -> executeQuery(query, jdbcTemplate));
                }
            });
            jdbcTemplate.getDataSource().getConnection().close();
        } else {
            throw new NonWhitelistedDatabaseException();
        }
    }

    public JdbcTemplate createJdbcTemplate(String server, String database, String username, String password) {
        DataSource dataSource = createDatasource(server,database,username,password);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        alterSession(jdbcTemplate);
        return jdbcTemplate;
    }

    private DataSource createDatasource(String server, String database, String username, String password) {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("oracle.jdbc.OracleDriver");
        dataSourceBuilder.url("jdbc:oracle:thin:@" + server + ":1521/" + database);
        dataSourceBuilder.username(username);
        dataSourceBuilder.password(password);
        return dataSourceBuilder.build();
    }


    private void alterSession(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.execute("alter session set nls_date_format=\"YYYY-MM-DD HH24:MI:SS\"");
        jdbcTemplate.execute("alter session set nls_timestamp_format=\"YYYY-MM-DD HH24:MI:SS\"");
    }
}
