package no.nav.pensjon.testdata.repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import no.nav.pensjon.testdata.controller.support.Handlebar;
import no.nav.pensjon.testdata.repository.support.PathUtil;
import no.nav.pensjon.testdata.repository.support.TestScenario;

@Repository
public class FileRepository {

    private static final Logger logger = LoggerFactory.getLogger(FileRepository.class);

    @Autowired
    private ScenarioRepository scenarioRepository;

    private final Map<String, List<Handlebar>> handleBars = new ConcurrentHashMap<>();

    public List<String> readSqlFile(String sqlFile) throws  IOException {
        Path path = PathUtil.readPath( sqlFile + ".sql");
        if (Files.exists(path)) {
            String allSql = new String(Files.readAllBytes(path));
            return Arrays.asList(allSql.split(System.getProperty("line.separator")));
        } else {
            throw new FileNotFoundException("Could not find SQL file: " + path);
        }
    }

    public String readSqlFileAsString(String sqlFile) throws  IOException {
        Path path = PathUtil.readPath(sqlFile + ".sql");
        if (Files.exists(path)) {
            return new String(Files.readAllBytes(path)).replace("\n", " ").replace("\r", " ");
        } else {
            throw new FileNotFoundException("Could not find SQL file: " + path);
        }
    }

    public List<TestScenario> getAllTestcases() throws IOException {
        List<TestScenario> allScenarios = new ArrayList<>();
        for (File file : PathUtil.readPath("scenario/").toFile().listFiles()) {
            if (file.isDirectory()) {

                logger.info("Trying to read: " + file.toString() + "/scenario.json");

                TestScenario scenario = scenarioRepository.getObjectMapper()
                        .readValue(PathUtil.readPath(file.toString() + "/scenario.json").toFile(), TestScenario.class);
                allScenarios.add(scenario);
            }
        }
        return allScenarios;
    }

    public List<Handlebar> getTestcaseHandlebars(String scenarioId) throws IOException {
        TestScenario scenario = scenarioRepository.getTestScenario(scenarioId);
        return handleBars.computeIfAbsent(scenario.getScenarioId(), s -> fetchHandlebars(scenario.getAllSql()));
    }

    private List<Handlebar> fetchHandlebars(String sql) {
        logger.info(sql);
        Set<Handlebar> handleBars = new LinkedHashSet<>();
        Matcher m = Pattern.compile("'\\{(.{1,30})\\}{1}'").matcher(sql);
        while (m.find()) {
            String group = m.group();
            logger.info(group);
            String bar = group.replace("'", "").replace("{", "").replace("}", "");
            String[] handleBarValidators = StringUtils.split(bar, "|");
            String handlebar = handleBarValidators[0];

            List<String> validators = Collections.emptyList();
            if (handleBarValidators.length > 1){
                validators = Stream.of(StringUtils.split(handleBarValidators[1], ";"))
                        .map(String::toLowerCase)
                        .collect(Collectors.toList());
            }
            logger.info(handlebar + " vs " + validators);
            handleBars.add(new Handlebar(handlebar, validators));
        }
        return new ArrayList<>(handleBars);
    }
}
