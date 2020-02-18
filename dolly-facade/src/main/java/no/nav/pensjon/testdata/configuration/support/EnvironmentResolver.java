package no.nav.pensjon.testdata.configuration.support;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnvironmentResolver {

    private static String CONFIGURATION_FILE = "available-environments.json";

    private static Map<String, Environment> envCache;

    public static Map<String, Environment> getAvaiableEnvironments()  {
        if (envCache != null) {
            return envCache;
        } else {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                ClassPathResource resource = new ClassPathResource(CONFIGURATION_FILE);
                List<Environment> config = objectMapper
                        .readValue(resource.getFile(), new TypeReference<List<Environment>>() {
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

    public static void erAlleMiljoerTilgjengelig(List<String> miljoer) {
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

    private static boolean erMiljoTilgjengelig(String miljo) {
        return getAvaiableEnvironments().get(miljo) != null;
    }
}
