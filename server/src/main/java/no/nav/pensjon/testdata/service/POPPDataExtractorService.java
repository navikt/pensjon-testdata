package no.nav.pensjon.testdata.service;

import no.nav.pensjon.testdata.configuration.support.JdbcTemplateWrapper;
import no.nav.pensjon.testdata.repository.FileRepository;
import no.nav.pensjon.testdata.repository.OracleRepository;
import no.nav.pensjon.testdata.repository.support.ComponentCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class POPPDataExtractorService {

    Logger logger = LoggerFactory.getLogger(POPPDataExtractorService.class);

    @Autowired
    OracleRepository oracleRepository;

    @Autowired
    private JdbcTemplateWrapper jdbcTemplateWrapper;

    @Autowired
    public FileRepository fileRepository;

    public List<String> extractDataFromPOPP(String fnr) throws IOException {
        logger.info("Started to extract data from POPP");
        oracleRepository.alterSession();
        String sqlSource = fileRepository.readSqlFileAsString("/popp-extract-data");

        List<String> sqlQueryList = Arrays.asList(sqlSource.split("#"));

        List<String> allInserts = new ArrayList<>();
        for (String initialSql : sqlQueryList) {
            String sql = initialSql.replace("{fnr}", fnr);
            List<Map<String, Object>> result = jdbcTemplateWrapper.queryForList(ComponentCode.POPP, sql);
            result.stream()
                    .map(map -> (String) map.entrySet().iterator().next().getValue())
                    .map(value -> value.replace("''","null"))
                    .forEach(allInserts::add);
        }
        logger.info("Completed extraction of data from POPP");
        return allInserts;
    }

    public void setFileRepository(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }
}
