package no.nav.pensjon.testdata.repository.support.validators;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
public abstract class AbstractHandlebarValidator {

    private String type;

    public abstract void validate(String value) throws ScenarioValidationException;

    public abstract String getErrorMessage();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public abstract String getDescription();
}
