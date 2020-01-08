package no.nav.pensjon.testdata.configuration;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SecretUtil {
    public static String readSecret(String path) throws IOException {
        String basedir = System.getProperty("secret.basedir");

        Path basepath;
        if (!StringUtils.isEmpty(basedir)) {
            basepath = Paths.get(basedir);
        } else {
            basepath = FileSystems.getDefault().getPath( System.getProperty("user.dir")+"/server");
        }

        Path secretPath = basepath.resolve("secrets").resolve(path);
        return String.join("\n", Files.readAllLines(secretPath));
    }
}
