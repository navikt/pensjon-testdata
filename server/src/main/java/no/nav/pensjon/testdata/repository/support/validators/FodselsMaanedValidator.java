package no.nav.pensjon.testdata.repository.support.validators;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import no.nav.pensjon.testdata.repository.support.Person;

import java.time.LocalDate;

public class FodselsMaanedValidator {
    @JsonProperty
    private int maaned;
    @JsonProperty
    private int aar;

    public void validate(Person person) throws ScenarioValidationException {
        LocalDate fodselsdato = person.getFodselsDato();
        if (fodselsdato != null) {
            if (fodselsdato.getMonth().getValue() != maaned || fodselsdato.getYear() != aar) {
                throw new ScenarioValidationException(getErrorMessage());
            }
        }
    }

    @JsonIgnore
    public String getErrorMessage() {
        return "Bruker er ikke født i riktig måned/år, må være født: "  + printMaaned() + "/" + aar;
    }

    private String printMaaned(){
        return String.valueOf(maaned).length() == 1 ? "0" + maaned : "" + maaned;
    }

    @JsonIgnore
    public String getAarMaaned(){
        return aar + "-" + printMaaned();
    }
}
