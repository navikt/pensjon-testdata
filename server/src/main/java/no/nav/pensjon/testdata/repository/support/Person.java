package no.nav.pensjon.testdata.repository.support;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import no.nav.pensjon.testdata.configuration.support.JdbcTemplateWrapper;
import no.nav.pensjon.testdata.repository.support.validators.AbstractScenarioValidator;
import no.nav.pensjon.testdata.repository.support.validators.ScenarioValidationException;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties({"nyPersonId","finnesIDatabase","fodselsDato"})
public class Person {
    private String key;
    private String gammelPersonId;
    private String nyPersonId;
    private boolean finnesIDatabase;
    private LocalDate fodselsDato;

    @JsonProperty("kontroller")
    private List<AbstractScenarioValidator> kontroller;

    public String getGammelPersonId() {
        return gammelPersonId;
    }

    public String getNyPersonId() {
        return nyPersonId;
    }

    public void setGammelPersonId(String gammelPersonId) {
        this.gammelPersonId = gammelPersonId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isFinnesIDatabase() {
        return finnesIDatabase;
    }

    public void setFinnesIDatabase(boolean finnesIDatabase) {
        this.finnesIDatabase = finnesIDatabase;
    }

    public void init(Map<String, String> handlebars, ComponentCode component, JdbcTemplateWrapper jdbcTemplateWrapper) {
        String newFnr = handlebars.get(this.key);
        List<Map<String, Object>> person = jdbcTemplateWrapper.queryForList(component, "SELECT * FROM T_PERSON WHERE FNR_FK = '" + newFnr + "'");

        if (!person.isEmpty()) {
            this.finnesIDatabase = true;
            BigDecimal personIdFromDatabase = (BigDecimal) person.get(0).get("PERSON_ID");
            Timestamp fDato = (Timestamp) person.get(0).get("DATO_FODSEL");
            if (fDato != null) {
                this.fodselsDato = fDato.toLocalDateTime().toLocalDate();
            }
            this.nyPersonId = personIdFromDatabase.toString();
        }

    }

    public void validate() throws ScenarioValidationException {
        if (kontroller != null) {
            for (AbstractScenarioValidator validator : kontroller) {
                validator.validate(this);
            }
        }
    }

    public LocalDate getFodselsDato() {
        return fodselsDato;
    }

    public void setFodselsDato(LocalDate fodselsDato) {
        this.fodselsDato = fodselsDato;
    }
}
