package no.nav.pensjon.testdata.repository;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pensjon.testdata.configuration.SecretUtil;
import no.nav.pensjon.testdata.configuration.support.JdbcTemplateWrapper;
import no.nav.pensjon.testdata.controller.support.NonWhitelistedDatabaseException;
import no.nav.pensjon.testdata.repository.support.ComponentCode;
import no.nav.pensjon.testdata.service.support.HandlebarTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class OracleRepository {
    private final Logger logger = LoggerFactory.getLogger(OracleRepository.class);

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private JdbcTemplateWrapper jdbcTemplateWrapper;

    @Transactional
    public void clearDatabase() throws IOException, NonWhitelistedDatabaseException {
        alterSession();
        if (canDatabaseBeCleared()) {
            logger.info("Removing all data from database");

            List<String> sql = fileRepository.readSqlFile("/unload");
            sql.forEach(query -> jdbcTemplateWrapper.execute(ComponentCode.PEN, query));
            logger.info("All data from database cleared");
        } else {
            throw new NonWhitelistedDatabaseException();
        }
    }

    @Transactional
    public void clearDatabaseForPerson(String fnr) throws IOException {
        alterSession();
        String sqlFile = fileRepository.readSqlFileAsString("/clear-oracle-data-for-person");

        //PEN
        logger.info("Fjerner data i PEN for person");
        Map<String, String> penParameters  = new HashMap<String, String>() {{
            put("component", "PEN");
            put("fnr", fnr);
        }};
        jdbcTemplateWrapper.execute(ComponentCode.PEN, HandlebarTransformer.execute(sqlFile, penParameters));

        //POPP
        logger.info("Fjerner data i POPP for person");
        Map<String, String> poppParameters  = new HashMap<String, String>() {{
            put("component", "POPP");
            put("fnr", fnr);
        }};
        jdbcTemplateWrapper.execute(ComponentCode.POPP,HandlebarTransformer.execute(sqlFile, poppParameters));

        //SAM
        logger.info("Fjerner data i SAM for person");
        Map<String, String> samParameters  = new HashMap<String, String>() {{
            put("component", "SAM");
            put("fnr", fnr);
        }};
        jdbcTemplateWrapper.execute(ComponentCode.SAM,HandlebarTransformer.execute(sqlFile, samParameters));

        logger.info("Fjerning av data ferdigstillt");
    }

    public void alterSession() {
        jdbcTemplateWrapper.execute(ComponentCode.PEN,"alter session set nls_date_format=\"YYYY-MM-DD HH24:MI:SS\"");
        jdbcTemplateWrapper.execute(ComponentCode.PEN, "alter session set nls_timestamp_format=\"YYYY-MM-DD HH24:MI:SS\"");
        jdbcTemplateWrapper.execute(ComponentCode.PEN, "alter session set nls_numeric_characters=\", \"");
        jdbcTemplateWrapper.execute(ComponentCode.PEN, "alter session set current_schema = PEN");

        jdbcTemplateWrapper.execute(ComponentCode.POPP, "alter session set nls_date_format=\"YYYY-MM-DD HH24:MI:SS\"");
        jdbcTemplateWrapper.execute(ComponentCode.POPP,"alter session set nls_timestamp_format=\"YYYY-MM-DD HH24:MI:SS\"");
        jdbcTemplateWrapper.execute(ComponentCode.POPP,"alter session set nls_numeric_characters=\", \"");
        jdbcTemplateWrapper.execute(ComponentCode.POPP, "alter session set current_schema = POPP");

        jdbcTemplateWrapper.execute(ComponentCode.SAM,"alter session set nls_date_format=\"YYYY-MM-DD HH24:MI:SS\"");
        jdbcTemplateWrapper.execute(ComponentCode.SAM,"alter session set nls_timestamp_format=\"YYYY-MM-DD HH24:MI:SS\"");
        jdbcTemplateWrapper.execute(ComponentCode.SAM,"alter session set nls_numeric_characters=\", \"");
        jdbcTemplateWrapper.execute(ComponentCode.SAM, "alter session set current_schema = SAM");

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
        boolean canClearDb = databaseWhiteList.stream().anyMatch(server::contains);
        if (canClearDb) {
            logger.info("Found that db: " + server + " can be cleared, as it is in db whitelist");
        } else {
            logger.info("Found that db: " + server + " can NOT be cleared, as it is in db whitelist");
        }
        return canClearDb;
    }
}
