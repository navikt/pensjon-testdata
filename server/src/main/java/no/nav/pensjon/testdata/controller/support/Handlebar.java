package no.nav.pensjon.testdata.controller.support;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Handlebar {
    String handlebar;
    List<String> validators;

    public Handlebar(String handlebar, List<String> validators) {
        this.handlebar = handlebar;
        this.validators = validators;
    }

    public String getHandlebar() {
        return handlebar;
    }

    public void setHandlebar(String handlebar) {
        this.handlebar = handlebar;
    }

    public List<String> getValidators() {
        return validators;
    }

    public void setValidators(List<String> validators) {
        this.validators = validators;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Handlebar handlebar1 = (Handlebar) o;

        return new EqualsBuilder()
                .append(handlebar, handlebar1.handlebar)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(handlebar)
                .toHashCode();
    }
}
