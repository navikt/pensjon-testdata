package no.nav.pensjon.testdata.configuration.support;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnvironmentResolver {


    private static Map<String, Environment> envCache;

    public static Map<String, Environment> getAvaiableEnvironments()  {
        if (envCache != null) {
            return envCache;
        } else {
            try {
                ObjectMapper objectMapper = new ObjectMapper();

                ResourceLoader resourceLoader = new DefaultResourceLoader();
                Resource resource = resourceLoader.getResource("classpath:available-environments.json");
                InputStream envInputStream = resource.getInputStream();

                List<Environment> config = objectMapper
                        .readValue(envInputStream, new TypeReference<List<Environment>>() {
                        });
                Map<String, Environment> allEnvironments = new HashMap<>();
                config
                        .stream()
                        .forEach(env -> allEnvironments.put(env.getEnv(), env));

                envCache = allEnvironments;
                return envCache;
            } catch (Exception e) {
                return new HashMap<>();
            }
        }
    }

    public static void erAlleMiljoerTilgjengelig(List<String> miljoer) throws IOException {
        if (miljoer == null || miljoer.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Ingen miljøer angitt ", null);
        }
        List<String> utilgjengeligeTestmiljo = new ArrayList<>();
        for (String miljo : miljoer) {
            if (!erMiljoTilgjengelig(miljo)) {
                utilgjengeligeTestmiljo.add(miljo);
            }
        }
        if (!utilgjengeligeTestmiljo.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Ikke mulig å opprette testdata mot " + String.join(",", utilgjengeligeTestmiljo), null);
        }

    }

    private static boolean erMiljoTilgjengelig(String miljo) throws IOException {
        return getAvaiableEnvironments().get(miljo) != null;
    }
}
