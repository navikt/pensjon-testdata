package no.nav.pensjon.testdata.repository;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pensjon.testdata.configuration.SecretUtil;
import no.nav.pensjon.testdata.controller.support.NonWhitelistedDatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Repository
public class OracleRepository {
    private final Logger logger = LoggerFactory.getLogger(OracleRepository.class);

    @Autowired
    FileRepository fileRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("poppJdbcTemplate")
    JdbcTemplate jdbcTemplatePopp;

    @Autowired
    @Qualifier("samJdbcTemplate")
    JdbcTemplate jdbcTemplateSam;

    public void executeQuery(String query) {
        logger.info(query);
        jdbcTemplate.execute(query);
    }

    @Transactional
    public void clearDatabase() throws IOException, NonWhitelistedDatabaseException {
        alterSession();
        if (canDatabaseBeCleared()) {
            logger.info("Removing all data from database");

            List<String> sql = fileRepository.readSqlFile("/unload");
            sql.forEach(query -> jdbcTemplate.execute(query));
            logger.info("All data from database cleared");
        } else {
            throw new NonWhitelistedDatabaseException();
        }
    }

    public void alterSession() {
        jdbcTemplate.execute("alter session set nls_date_format=\"YYYY-MM-DD HH24:MI:SS\"");
        jdbcTemplate.execute("alter session set nls_timestamp_format=\"YYYY-MM-DD HH24:MI:SS\"");
        jdbcTemplate.execute("alter session set nls_numeric_characters=\", \"");

        jdbcTemplatePopp.execute("alter session set nls_date_format=\"YYYY-MM-DD HH24:MI:SS\"");
        jdbcTemplatePopp.execute("alter session set nls_timestamp_format=\"YYYY-MM-DD HH24:MI:SS\"");
        jdbcTemplatePopp.execute("alter session set nls_numeric_characters=\", \"");

        jdbcTemplateSam.execute("alter session set nls_date_format=\"YYYY-MM-DD HH24:MI:SS\"");
        jdbcTemplateSam.execute("alter session set nls_timestamp_format=\"YYYY-MM-DD HH24:MI:SS\"");
        jdbcTemplateSam.execute("alter session set nls_numeric_characters=\", \"");
    }

    public boolean canDatabaseBeCleared() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ClassPathResource resource = new ClassPathResource("clear-database-whiteliste.json");
        List<String> databaseWhiteList = mapper.readValue(resource.getFile(), new TypeReference<List<String>>() {
        });
        String server = SecretUtil.readSecret("db/pen/jdbc_url");
        return isWhitelistedDatabase(server, databaseWhiteList);
    }

    private boolean isWhitelistedDatabase(String server, List<String> databaseWhiteList) {
        boolean canClearDb = databaseWhiteList.stream().anyMatch(element -> server.contains(element));
                if (canClearDb) {
                    logger.info("Found that db: " + server + " can be cleared, as it is in db whitelist");
                } else {
                    logger.info("Found that db: " + server + " can NOT be cleared, as it is in db whitelist");
                }
        return canClearDb;
    }
}
