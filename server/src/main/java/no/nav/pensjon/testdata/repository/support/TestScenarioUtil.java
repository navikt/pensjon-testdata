package no.nav.pensjon.testdata.repository.support;

import no.nav.pensjon.testdata.repository.support.validators.ScenarioValidationException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TestScenarioUtil {
    private TestScenarioUtil(){}

    public static Map<String, String> getAllePersonIds(TestScenario testScenario) {
        return getAllePersoner(testScenario).stream()
                .collect(Collectors.toMap(Person::getGammelPersonId, Person::getNyPersonId));
    }

    public static List<Person> getAllePersoner(TestScenario testScenario){
        return testScenario.getComponents().stream()
                .flatMap(c -> c.getPersoner().stream())
                .collect(Collectors.toList());
    }

    public static String getAllSql(TestScenario testScenario) {
        StringBuilder sb = new StringBuilder();
        testScenario.getComponents()
                .stream()
                .forEach(component -> sb.append(component.getSqlAsString(testScenario.getScenarioId())));
        return sb.toString();
    }

    public static void validate(TestScenario testScenario) throws ScenarioValidationException {
        for (Component component : testScenario.getComponents()) {
            for (Person person : component.getPersoner()) {
                person.validate();
            }
        }
    }
}
