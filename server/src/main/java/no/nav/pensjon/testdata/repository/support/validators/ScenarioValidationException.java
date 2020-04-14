package no.nav.pensjon.testdata.repository.support.validators;

public class ScenarioValidationException extends RuntimeException {

    public ScenarioValidationException(String errorMessage) {
        super(errorMessage);
    }
}
