package no.nav.pensjon.testdata.repository;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pensjon.testdata.repository.support.TestScenario;
import no.nav.pensjon.testdata.repository.support.PrimaryKeySwapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
public class FileRepository {

    @Autowired
    private ScenarioRepository scenarioRepository;

    public List<String> readSqlFile(String sqlFile) throws  IOException {
        ClassPathResource resource = new ClassPathResource(sqlFile + ".sql");
        Path path = Paths.get(resource.getFile().getPath());
        if (Files.exists(path)) {
            String allSql = new String(Files.readAllBytes(path));
            return Arrays.asList(allSql.split(System.getProperty("line.separator")));
        } else {
            throw new FileNotFoundException("Could not find SQL file: " + path);
        }
    }

    public String readSqlFileAsString(String sqlFile) throws  IOException {
        ClassPathResource resource = new ClassPathResource(sqlFile + ".sql");
        Path path = Paths.get(resource.getFile().getPath());
        if (Files.exists(path)) {
            return new String(Files.readAllBytes(path)).replace("\n", " ").replace("\r", " ");
        } else {
            throw new FileNotFoundException("Could not find SQL file: " + path);
        }
    }

    public List<String> getAllTestcases() throws IOException {
        ClassPathResource resource = new ClassPathResource("/scenario/");
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> allScenarios = new ArrayList<>();
        for (File file : resource.getFile().listFiles()) {
            if (file.isDirectory()) {
                TestScenario scenario = scenarioRepository.getObjectMapper()
                        .readValue(Paths.get(file.toString(),"scenario.json").toFile(), TestScenario.class);
                allScenarios.add(scenario.getName());
            }
        }
        return allScenarios;
    }

    public Set<String> getTestcaseHandlebars(String scenarioId) throws IOException {
        TestScenario scenario = scenarioRepository.getTestScenario(scenarioId);
        return getHandlebars(scenario.getAllSql());
    }

    private static Set<String> getHandlebars(String sql) {
        Set<String> oldPrimaryKeys = new HashSet<>();
        Matcher m = Pattern.compile("'\\{(.{1,30})\\}{1}'").matcher(sql);
        while (m.find()) {
            String group = m.group();
            oldPrimaryKeys.add(group.replace("'","").replace("{","").replace("}",""));
        }
        return oldPrimaryKeys;
    }
}
