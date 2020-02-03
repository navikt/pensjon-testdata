package no.nav.pensjon.testdata.service;

import no.nav.pensjon.testdata.repository.FileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class POPPDataExtractor {

    Logger logger = LoggerFactory.getLogger(POPPDataExtractor.class);

    @Autowired
    @Qualifier("poppJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public FileRepository fileRepository;

    public List<String> extractDataFromPOPP(String fnr) throws IOException {
        logger.info("Started to extract data from POPP");
        List<String> sqlQueryList = fileRepository.readSqlStatements("/popp-extract-data");
        List<String> allInserts = new ArrayList<>();
        for (String initialSql : sqlQueryList) {
            String sql = initialSql.replace("{fnr}", fnr);
            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
            result.stream()
                    .map(map -> (String) map.entrySet().iterator().next().getValue())
                    .map(value -> value.replace("''","null"))
                    .forEach(allInserts::add);
        }
        allInserts.stream().forEach(logger::info);
        logger.info("Completed extraction of data from POPP");
        return allInserts;
    }

    public void setFileRepository(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }
}
