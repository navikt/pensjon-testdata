package no.nav.pensjon.testdata.repository.support.validators;

import com.fasterxml.jackson.annotation.JsonIgnore;
import no.nav.pensjon.testdata.repository.support.Person;

import java.time.LocalDate;

public class FodselsMaanedValidator extends AbstractScenarioValidator {
    private int maaned;
    private int aar;

    public FodselsMaanedValidator() {
    }

    @Override
    public void validate(Person person) throws ScenarioValidationException {
        LocalDate fodselsdato = person.getFodselsDato();
        if (fodselsdato != null) {
            if (fodselsdato.getMonth().getValue() != maaned || fodselsdato.getYear() != aar) {
                throw new ScenarioValidationException(getErrorMessage());
            }
        }
    }

    @Override
    @JsonIgnore
    public String getErrorMessage() {
        return "Bruker er ikke født i riktig måned/år, må være født: "  + printMaaned() + "/" + aar;
    }

    @Override
    @JsonIgnore
    public String getDescription() {
        return "Bruker må være født i måned/år: " + printMaaned() + "/" + aar;
    }

    private String printMaaned(){
        return String.valueOf(maaned).length() == 1 ? "0" + maaned : "" + maaned;
    }

    public int getMaaned() {
        return maaned;
    }

    public void setMaaned(int maaned) {
        this.maaned = maaned;
    }

    public int getAar() {
        return aar;
    }

    public void setAar(int aar) {
        this.aar = aar;
    }
}
