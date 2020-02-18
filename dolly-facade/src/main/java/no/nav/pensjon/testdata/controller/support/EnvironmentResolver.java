package no.nav.pensjon.testdata.controller.support;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pensjon.testdata.configuration.support.Environment;
import org.springframework.core.io.ClassPathResource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnvironmentResolver {

    public static Map<String, Environment> getAvaiableEnvironments()  {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ClassPathResource resource = new ClassPathResource("available-environments.json");
            List<Environment> config = objectMapper
                    .readValue(resource.getFile(), new TypeReference<List<Environment>>() {
                    });
            Map<String, Environment> allEnvironments = new HashMap<>();
            config
                    .stream()
                    .forEach(env -> allEnvironments.put(env.getEnv(), env));
            return allEnvironments;
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
}
