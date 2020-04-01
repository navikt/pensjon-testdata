package no.nav.pensjon.testdata.repository.support;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.pensjon.testdata.repository.support.validators.ScenarioValidationException;

public class TestScenario {
    private String scenarioId;
    private String name;
    private String fritekstbeskrivelse = null;
    private List<Component> components;

    @JsonCreator
    public TestScenario(
            @JsonProperty("scenarioId") String scenarioId,
            @JsonProperty("name") String name,
            @JsonProperty("fritekstbeskrivelse") String fritekstbeskrivelse,
            @JsonProperty("components") List<Component> components) {
        this.scenarioId = scenarioId;
        this.name = name;
        this.fritekstbeskrivelse = fritekstbeskrivelse;
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

    public String getFritekstbeskrivelse() {
        return fritekstbeskrivelse;
    }

    public Map<String, String> getAllePersonIds() {
        return getAllePersoner().stream()
                .collect(Collectors.toMap(Person::getGammelPersonId, Person::getNyPersonId));
    }

    public List<Person> getAllePersoner(){
        return components.stream()
                .flatMap(c -> c.getPersoner().stream())
                .collect(Collectors.toList());
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
