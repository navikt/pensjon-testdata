package no.nav.pensjon.testdata.service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PersonIdExtractor {

    public static String execute(List<String> statements) {
        //WAAAY to simple to handle multiple users, and complex scenarios. But will do for now..
        Optional<String> sql = statements.stream()
                .filter(statement -> statement.contains("insert into \"PEN\".\"T_PERSON\"")).findFirst();
        if (sql.isPresent()) {
            String query = sql.get();
            Matcher m = Pattern.compile("values\\s\\('(?<userId>\\d*?)'").matcher(query);
            while (m.find()) {
                return m.group("userId");
            }
        }
        return null;
    }
}
