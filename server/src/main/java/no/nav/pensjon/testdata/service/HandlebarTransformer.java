package no.nav.pensjon.testdata.service;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class HandlebarTransformer {

    public static String execute(String sql, Map<String,String> handlebars) {
        if (handlebars != null && handlebars.size() > 0) {
            return StringUtils.replaceEach(sql, handlebars
                    .keySet()
                    .stream()
                    .map(key -> "{" + key + "}").toArray(String[]::new), handlebars.values().toArray(new String[0]));
        } else {
            return sql;
        }
    }
}
