package no.nav.pensjon.testdata.service.support;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class HandlebarTransformer {

    public static String execute(String sql, Map<String,String> handlebars) {
        if (handlebars != null && handlebars.size() > 0) {
            Map<String, String> handlebarSqlKeys = new HashMap<>();
            for (Map.Entry<String, String> handlebar : handlebars.entrySet()){
                String key = handlebar.getKey();
                String unprocessedSql = sql;
                while (unprocessedSql.contains("{" + key)){
                    unprocessedSql = StringUtils.substringAfter(unprocessedSql, "{" + key);
                    String replaceKey = "{" + key + StringUtils.substringBefore(unprocessedSql, "'");
                    handlebarSqlKeys.put(key, replaceKey);
                }
            }
            return StringUtils.replaceEach(sql, handlebars
                    .keySet()
                    .stream()
                    .map(handlebarSqlKeys::get)
                    .toArray(String[]::new), handlebars.values().toArray(new String[0]));
        } else {
            return sql;
        }
    }
}
