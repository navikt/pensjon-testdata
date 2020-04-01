package no.nav.pensjon.testdata.repository.support.validators;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import no.nav.pensjon.testdata.repository.support.Person;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = FodselsMaanedValidator.class, name = "fodselMaaned"),

})
public abstract class AbstractScenarioValidator {

    private String type;

    public abstract void validate(Person person) throws ScenarioValidationException;

    public abstract String getErrorMessage();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public abstract String getDescription();
}
