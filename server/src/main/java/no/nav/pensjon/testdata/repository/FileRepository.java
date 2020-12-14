package no.nav.pensjon.testdata.repository;

import no.nav.pensjon.testdata.controller.support.Handlebar;
import no.nav.pensjon.testdata.repository.support.PathUtil;
import no.nav.pensjon.testdata.repository.support.TestScenario;
import no.nav.pensjon.testdata.repository.support.TestScenarioUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class FileRepository {

    @Autowired
    private ScenarioRepository scenarioRepository;

    private final Map<Integer, List<Handlebar>> handleBars = new ConcurrentHashMap<>();

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

    public List<Handlebar> getTestcaseHandlebars(int scenarioId) throws IOException {
        TestScenario scenario = scenarioRepository.obtainScenarioCopy(scenarioId);
        return handleBars.computeIfAbsent(scenario.getScenarioId(), s -> fetchHandlebars(TestScenarioUtil.getAllSql(scenario)));
    }

    private List<Handlebar> fetchHandlebars(String sql) {
        Set<Handlebar> handleBars = new LinkedHashSet<>();
        Matcher m = Pattern.compile("'\\{(.{1,30})\\}{1}'").matcher(sql);
        while (m.find()) {
            String group = m.group();
            String bar = group.replace("'", "").replace("{", "").replace("}", "");
            String[] handleBarSpecs = StringUtils.split(bar, "|");

            Handlebar handlebar = new Handlebar(handleBarSpecs[0]);
            if (handleBarSpecs.length > 1){
                handlebar.setInputtype(handleBarSpecs[1]);
            }

            if (handleBarSpecs.length > 2){
                List<String> validators = Stream.of(StringUtils.split(handleBarSpecs[2], ";"))
                        .map(String::toLowerCase)
                        .collect(Collectors.toList());
                handlebar.setValidators(validators);
            }
            handleBars.add(handlebar);
        }
        return new ArrayList<>(handleBars);
    }
}
