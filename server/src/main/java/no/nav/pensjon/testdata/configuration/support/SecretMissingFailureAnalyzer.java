package no.nav.pensjon.testdata.configuration.support;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

import static java.lang.String.format;

public class SecretMissingFailureAnalyzer extends AbstractFailureAnalyzer<SecretMissingException> {
    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, SecretMissingException cause) {
        return new FailureAnalysis(getDescription(cause), getAction(cause), cause);
    }

    private String getDescription(SecretMissingException e) {
        return format("The secret file '%s' is missing", e.getName());
    }

    private String getAction(SecretMissingException e) {
        return format("Create missing secret file '%s'. You may use the script 'create-secrets-for-dev.sh' to create initial files", e.getPath());
    }
}
