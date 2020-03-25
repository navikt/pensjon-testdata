package no.nav.pensjon.testdata.repository.support;

import org.apache.commons.lang3.StringUtils;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathUtil {
    public static Path readPath(String path) {
        String basedir = System.getenv().getOrDefault("SECRET_BASEDIR", System.getProperty("secret.basedir"));

        Path basepath;
        if (!StringUtils.isEmpty(basedir)) {
            basepath = Paths.get(basedir).resolve("/app/sql");
        } else {
            basepath = FileSystems.getDefault().getPath( System.getProperty("user.dir")+"/server/sql");
        }

        Path foundPath = basepath.resolve(path);
        if (!Files.exists(foundPath)) {
            throw new RuntimeException("Fant ikke forespurt fil: " + foundPath);
        }
        return foundPath;
    }
}
