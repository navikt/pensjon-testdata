package no.nav.pensjon.testdata.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.nav.pensjon.testdata.repository.support.validators.FnrValidator;

@Service
public class ValidationService {
    @Autowired
    private List<FnrValidator> fnrValidators;

    public void validate(Map<String, String> handlebars){
        for (Map.Entry<String, String> handlebar : handlebars.entrySet()){
            String handlebarName = Optional.ofNullable(handlebar.getKey()).map(String::toLowerCase).orElse("");
            String value = handlebar.getValue();

            switch (handlebarName){
                case "fnr_avdod_ektefelle":
                case "fnr": fnrValidators.forEach(v -> v.validate(value));
            }
        }
    }
}
