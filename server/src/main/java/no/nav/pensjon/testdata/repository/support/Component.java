package no.nav.pensjon.testdata.repository.support;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties({"sql"})
public class Component {

    private ComponentCode component;
    private List<String> sqlPaths;
    private List<String> sql = new ArrayList<>();
    private List<Person> personer;

    public ComponentCode getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = ComponentCode.valueOf(component);
    }

    public List<String> getSqlPaths() {
        return sqlPaths;
    }

    public void setSqlPaths(List<String> sqlPaths) {
        this.sqlPaths = sqlPaths;
    }

    public List<Person> getPersoner() {
        return personer;
    }

    public void setPersoner(List<Person> personer) {
        this.personer = personer;
    }

    public List<String> getSql() {
        return this.sql;
    }

    public String getSqlAsString(String scenarioId) {
        StringBuilder sb = new StringBuilder();
        for (String file : this.sqlPaths) {
            ClassPathResource resource = new ClassPathResource("scenario/" + scenarioId + "/" + file);
            try {
                sb.append(new String(Files.readAllBytes(resource.getFile().toPath())));
            } catch (IOException e) {
                throw new RuntimeException("Fant ikke scenarie: " + scenarioId + "/" + file);
            }
        }
        return sb.toString();
    }

    public void init(String scenarioId)  {
        for (String file : this.sqlPaths) {
            ClassPathResource resource = new ClassPathResource("scenario/" + scenarioId + "/" + file);
            if (resource.exists()) {
                try {
                    String allSql = new String(Files.readAllBytes(resource.getFile().toPath()));
                    sql.addAll(Arrays.asList(allSql.split(System.getProperty("line.separator"))));
                } catch (IOException e) {
                    throw new RuntimeException("Fant ikke scenarie: " + scenarioId + "/" + file);
                }
            }
        }
    }
}