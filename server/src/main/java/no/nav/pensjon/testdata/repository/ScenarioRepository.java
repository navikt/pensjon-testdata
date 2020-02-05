package no.nav.pensjon.testdata.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pensjon.testdata.repository.support.Scenario;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@Repository
public class ScenarioRepository {

    public Scenario getScenario(String scenarioId) throws IOException {
        ClassPathResource resource = new ClassPathResource("/scenario/");
        ObjectMapper objectMapper = new ObjectMapper();
        for (File file : resource.getFile().listFiles()) {
            if (file.isDirectory() ) {
                Scenario scenario = objectMapper.readValue(Paths.get(file.toString(),"scenario.json").toFile(), Scenario.class);
                if (scenario.getName().equals(scenarioId)) {
                    scenario.setPenFileSrc(Paths.get(file.getPath(), scenario.getPenFile()).toFile());
                    scenario.setPoppFileSrc(Paths.get(file.getPath(), scenario.getPoppFile()).toFile());
                    return scenario;
                }
            }
        }
        throw  new RuntimeException("Could not find scenario!");
    }
}
