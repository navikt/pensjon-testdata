package no.nav.pensjon.testdata.repository.support.validators;

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
            if (fodselsdato.getMonth().getValue() != maaned && fodselsdato.getYear() != aar) {
                throw new ScenarioValidationException(getErrorMessage());
            }
        }
    }

    @Override
    public String getErrorMessage() {
        return "Bruker er ikke født i riktig måned/år, må være født: "  + maaned + "/" + aar  ;
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
