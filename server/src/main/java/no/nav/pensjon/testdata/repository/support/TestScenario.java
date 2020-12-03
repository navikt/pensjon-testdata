package no.nav.pensjon.testdata.repository.support;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TestScenario {
    private String scenarioId;
    private String name;
    private String fritekstbeskrivelse = null;
    private String saksType;
    private List<Component> components;

    @JsonCreator
    public TestScenario(
            @JsonProperty("scenarioId") String scenarioId,
            @JsonProperty("name") String name,
            @JsonProperty("fritekstbeskrivelse") String fritekstbeskrivelse,
            @JsonProperty("saksType") String saksType,
            @JsonProperty("components") List<Component> components) {
        this.scenarioId = scenarioId;
        this.name = name;
        this.fritekstbeskrivelse = fritekstbeskrivelse;
        this.saksType = saksType;
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

    public String getSaksType() {
        return saksType;
    }
}
