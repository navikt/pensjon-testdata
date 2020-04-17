package no.nav.pensjon.testdata.repository.support.validators;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import no.nav.pensjon.testdata.configuration.support.JdbcTemplateWrapper;
import no.nav.pensjon.testdata.repository.support.ComponentCode;

@Component
public class UbruktFnrValidator extends FnrValidator {

    @Autowired
    private JdbcTemplateWrapper jdbcTemplateWrapper;

    @Override
    public void validate(String fnr) throws ScenarioValidationException {
        List<Long> caseIds = Collections.emptyList();
        if (!StringUtils.isBlank(fnr)){
            caseIds = jdbcTemplateWrapper.queryForList(ComponentCode.PEN,
                    "SELECT s.SAK_ID FROM PEN.t_sak s, PEN.t_person p WHERE p.person_id = s.person_id AND p.fnr_fk = " + fnr,
                    (rs, rowNum) -> rs.getLong("SAK_ID"));
        }
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
