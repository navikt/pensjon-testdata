package no.nav.pensjon.testdata.repository;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PrimaryKeySwapper {

    public static String swapPrimaryKeysInSql(String sql) {
        List<String> oldPrimaryKeys = getPrimaryKeys(sql);
        removeExcludedIds(oldPrimaryKeys);
        List<String> newPrimaryKeys =
                oldPrimaryKeys
                        .stream()
                        .map(PrimaryKeySwapper::generateNewPrimaryKey)
                        .collect(Collectors.toList());




        return StringUtils.replaceEach(sql, oldPrimaryKeys.toArray(new String[0]), newPrimaryKeys.toArray(new String[0]));
    }

    private static void removeExcludedIds(List<String> primaryKeys) {
        String[] ignoreCandidates = {"100000007","65885471","150003452"};
        Arrays.stream(ignoreCandidates)
                .forEach( i -> primaryKeys.remove(i));
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
