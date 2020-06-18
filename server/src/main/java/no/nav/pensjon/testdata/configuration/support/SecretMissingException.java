package no.nav.pensjon.testdata.configuration.support;

import java.nio.file.Path;

public class SecretMissingException extends RuntimeException {
    private final String name;
    private final Path path;

    public SecretMissingException(final String name, final Path path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public Path getPath() {
        return path;
    }
}
