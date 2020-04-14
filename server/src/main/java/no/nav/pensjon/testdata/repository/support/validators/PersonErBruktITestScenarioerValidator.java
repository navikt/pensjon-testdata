package no.nav.pensjon.testdata.repository.support.validators;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import no.nav.pensjon.testdata.configuration.support.JdbcTemplateWrapper;
import no.nav.pensjon.testdata.repository.support.ComponentCode;
import no.nav.pensjon.testdata.repository.support.Person;

@Component
public class PersonErBruktITestScenarioerValidator extends AbstractScenarioValidator {

    @Autowired
    private JdbcTemplateWrapper jdbcTemplateWrapper;

    @Override
    public void validate(Person person) throws ScenarioValidationException {
        if (person != null && StringUtils.isNotBlank(person.getNyPersonId())){
            List<Long> caseIds = jdbcTemplateWrapper.queryForList(ComponentCode.PEN, "SELECT s.SAK_ID FROM PEN.t_sak s WHERE s.person_id = " + person.getNyPersonId(),
                    (rs, rowNum) -> rs.getLong("SAK_ID"));
            if (!caseIds.isEmpty()){
                throw new ScenarioValidationException(getErrorMessage() + ", eksisterende saksnumre: " + caseIds);
            }
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
