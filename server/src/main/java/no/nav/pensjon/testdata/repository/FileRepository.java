package no.nav.pensjon.testdata.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pensjon.testdata.service.Scenario;
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

    public List<String> readSqlStatements(String scenario) throws  IOException {
        ClassPathResource resource = new ClassPathResource(scenario + ".sql");
        Path path = Paths.get(resource.getFile().getPath());
        if (Files.exists(path)) {
            String allSql = new String(Files.readAllBytes(path));
            String allSqlWithNewPrimaryKeys = PrimaryKeySwapper.swapPrimaryKeysInSql(allSql);
            return Arrays.asList(allSqlWithNewPrimaryKeys.split(System.getProperty("line.separator")));
        } else {
            throw new FileNotFoundException("Could not find SQL file: " + path);
        }
    }

    public List<String> readSqlStatements(File scenario, String ... excludeId) throws  IOException {
        if (Files.exists(scenario.toPath())) {
            String allSql = new String(Files.readAllBytes(scenario.toPath()));
            String allSqlWithNewPrimaryKeys = PrimaryKeySwapper.swapPrimaryKeysInSql(allSql, excludeId);
            return Arrays.asList(allSqlWithNewPrimaryKeys.split(System.getProperty("line.separator")));
        } else {
            throw new FileNotFoundException("Could not find SQL file: " + scenario.toPath());
        }
    }

    public List<String> getAllTestcases() throws IOException {
        ClassPathResource resource = new ClassPathResource("/scenario/");
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> allScenarios = new ArrayList<>();
        for (File file : resource.getFile().listFiles()) {
            if (file.isDirectory()) {
                Scenario scenario = objectMapper.readValue(Paths.get(file.toString(),"scenario.json").toFile(), Scenario.class);
                allScenarios.add(scenario.getName());
            }
        }
        return allScenarios;
    }

    public Set<String> getTestcaseHandlebars(String scenario) throws IOException {
        ClassPathResource resource = new ClassPathResource("/scenario/");
        ObjectMapper objectMapper = new ObjectMapper();
        Path path = Paths.get(resource.getFile().getPath());
        for (File file : resource.getFile().listFiles()) {
            if (file.isDirectory()) {
                Scenario config = objectMapper.readValue(Paths.get(file.toString(),"scenario.json").toFile(), Scenario.class);
                if (config.getName().equals(scenario)) {
                    Set<String> allHandlebars = new HashSet<>();
                    for (File fileInScenario : file.listFiles()) {
                        if (fileInScenario.getName().contains(".sql")) {
                            String allSql = new String(Files.readAllBytes(fileInScenario.toPath()));
                            Set<String> handlebars = getHandlebars(allSql);
                            allHandlebars.addAll(handlebars);
                        }
                    }
                    return allHandlebars;
                }
            }
        }
        throw new FileNotFoundException("Could not find SQL file: " + path);
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
