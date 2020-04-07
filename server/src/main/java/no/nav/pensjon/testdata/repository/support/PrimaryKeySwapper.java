package no.nav.pensjon.testdata.repository.support;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

public class PrimaryKeySwapper {
    static Map<String, String> primaryKeyRegistry = new ConcurrentHashMap<>();

    public static void initializePrimaryKeyRegistry(Map<String, String> init) {
        primaryKeyRegistry.clear();
        primaryKeyRegistry.putAll(init);
    }

    public static String swapPrimaryKeysInSql(String sql) {
        List<String> oldPrimaryKeys = getPrimaryKeys(sql);

        oldPrimaryKeys.stream()
                .filter(key -> !primaryKeyRegistry.containsKey(key))
                .forEach(oldKey -> primaryKeyRegistry.put(oldKey, generateNewPrimaryKey(oldKey)));

        List<String> newPrimaryKeys = oldPrimaryKeys
                .stream()
                .map(oldPrimaryKey -> (primaryKeyRegistry.get(oldPrimaryKey)))
                .collect(Collectors.toList());
        return StringUtils.replaceEach(sql, oldPrimaryKeys.toArray(new String[0]), newPrimaryKeys.toArray(new String[0]));
    }

    public static void updatePrimaryKey(String key, String value){
        primaryKeyRegistry.put(key, value);
    }

    public static boolean containsPrimaryKey(String primaryKey){
        return primaryKeyRegistry.containsKey(primaryKey);
    }

    private static List<String> getPrimaryKeys(String sql) {
        Set<String> oldPrimaryKeys = new HashSet<>();
        Matcher m = Pattern.compile("'\\d{8,10}'").matcher(sql);
        while (m.find()) {
            String group = m.group().replace("'", "");
            oldPrimaryKeys.add(group);
        }
        return oldPrimaryKeys.stream().collect(Collectors.toList());
    }

    private static String generateNewPrimaryKey(String oldPrimaryKey) {
        Integer newPrimaryKey = Integer.valueOf(oldPrimaryKey) + 400000000 + (new Random()).nextInt(100000000);
        return newPrimaryKey.toString();
    }
}
