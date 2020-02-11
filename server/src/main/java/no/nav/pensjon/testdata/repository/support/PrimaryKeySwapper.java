package no.nav.pensjon.testdata.repository.support;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PrimaryKeySwapper {

    private static Map<String,String> primaryKeyRegistry = new HashMap<>();

    public static String swapPrimaryKeysInSql(String sql, String ... excludeId) {
        List<String> oldPrimaryKeys = getPrimaryKeys(sql);
        removeExcludedIds(oldPrimaryKeys, excludeId);

        for (String oldPrimaryKey : oldPrimaryKeys) {
            if (!primaryKeyRegistry.containsKey(oldPrimaryKey)){
                primaryKeyRegistry.put(oldPrimaryKey, generateNewPrimaryKey(oldPrimaryKey));
            }
        }
        List<String> newPrimaryKeys = oldPrimaryKeys
                .stream()
                .map(oldPrimaryKey -> (primaryKeyRegistry.get(oldPrimaryKey)))
                .collect(Collectors.toList());

        return StringUtils.replaceEach(sql, oldPrimaryKeys.toArray(new String[0]), newPrimaryKeys.toArray(new String[0]));
    }



    private static void removeExcludedIds(List<String> primaryKeys, String ... excludeId) {
        String[] penOrgEnhetIds = {"100000007","65885471","150003452"};
        Stream.concat(
                Arrays.stream(penOrgEnhetIds),
                Arrays.stream(excludeId))
                .forEach(key -> primaryKeys.remove(key));
    }

    private static List<String> getPrimaryKeys(String sql) {
        Set<String> oldPrimaryKeys = new HashSet<>();
        Matcher m = Pattern.compile("'\\d{8,10}'").matcher(sql);
        while (m.find()) {
            String group = m.group().replace("'","");
            oldPrimaryKeys.add(group);
        }
        return oldPrimaryKeys.stream().collect(Collectors.toList());
    }

    private static String generateNewPrimaryKey(String oldPrimaryKey) {
        Integer newPrimaryKey = Integer.valueOf(oldPrimaryKey) +  (new Random()).nextInt(50000000);
        return newPrimaryKey.toString();
    }
}
