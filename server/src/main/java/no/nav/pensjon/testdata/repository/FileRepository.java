package no.nav.pensjon.testdata.repository;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    public List<String> getAllTestcases() throws IOException {
        ClassPathResource resource = new ClassPathResource("/scenario/");
        return Files.list(Paths.get(resource.getFile().getPath()))
                .map(path -> path.getFileName().toString().replace(".sql", ""))
                .collect(Collectors.toList());
    }

    public Set<String> getTestcaseHandlebars(String scenario) throws IOException {
        ClassPathResource resource = new ClassPathResource("/scenario/" + scenario + ".sql");
        Path path = Paths.get(resource.getFile().getPath());
        if (Files.exists(path)) {
            String allSql = new String(Files.readAllBytes(path));

            return getHandlebars(allSql);
        } else {
            throw new FileNotFoundException("Could not find SQL file: " + path);
        }
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
