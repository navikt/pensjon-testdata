package no.nav.pensjon.testdata.repository.support;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import no.nav.pensjon.testdata.repository.support.validators.ScenarioValidationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestScenario {
    private String scenarioId;
    private String name;
    private List<Component> components;

    @JsonCreator
    public TestScenario(
            @JsonProperty("scenarioId") String scenarioId,
            @JsonProperty("name") String name,
            @JsonProperty("components") List<Component> components) {
        this.scenarioId = scenarioId;
        this.name = name;
        this.components = components;

        components.stream().forEach(component -> component.init(scenarioId));
    }

    public String getScenarioId() {
        return scenarioId;
    }

    public String getName() {
        return name;
    }

    public List<Component> getComponents() {
        return components;
    }

    public Map<String, String> getAllePersoner() {
        Map<String, String> allePersoner = new HashMap<>();
        components
                .stream()
                .forEach(component -> component.getPersoner()
                        .stream()
                        .forEach(person -> allePersoner.put(person.getGammelPersonId(), person.getNyPersonId())));

        return allePersoner;
    }

    public String getAllSql() {
        StringBuilder sb = new StringBuilder();
        components
                .stream()
                .forEach(component -> sb.append(component.getSqlAsString(this.scenarioId)));
        return sb.toString();
    }

    public void validate() throws ScenarioValidationException {
        for (Component component : components) {
            for (Person person : component.getPersoner()) {
                person.validate();
            }
        }
    }
}
