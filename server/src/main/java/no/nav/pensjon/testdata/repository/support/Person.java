package no.nav.pensjon.testdata.repository.support;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties({"nyPersonId","finnesIDatabase"})
public class Person {
    private String key;
    private String gammelPersonId;
    private String nyPersonId;
    private boolean finnesIDatabase;

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

    public void init(Map<String, String> handlebars, JdbcTemplate jdbcTemplate) {
        String newFnr = handlebars.get(this.key);
        List<Map<String, Object>> person = jdbcTemplate.queryForList("SELECT PERSON_ID FROM T_PERSON WHERE FNR_FK = '" + newFnr + "'");

        if (!person.isEmpty()) {
            this.finnesIDatabase = true;
            BigDecimal personIdFromDatabase = (BigDecimal) person.get(0).get("PERSON_ID");
            this.nyPersonId = personIdFromDatabase.toString();
        }

    }
}
