package no.nav.pensjon.testdata.repository.support.validators;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import no.nav.pensjon.testdata.repository.support.Person;
import no.nav.pensjon.testdata.service.TestdataService;

@Component
public class PersonErBruktITestScenarioerValidator extends AbstractScenarioValidator {

    @Autowired
    TestdataService testdataService;

    @Override
    public void validate(Person person) throws ScenarioValidationException {
        List<Long> caseIds = testdataService.fetchPersonsExistingCases(person.getNyPersonId());
        if (!caseIds.isEmpty()){
            throw new ScenarioValidationException(getErrorMessage() + ", eksisterende saksnumre: " + caseIds);
        }
    }

    @Override
    public String getErrorMessage() {
        return "Person er brukt i andre testscenarioer i miljøet";
    }

    @Override
    public String getDescription() {
        return "Person må ikke være brukt i andre testscenarioer i miljøet";
    }
}
