package no.nav.pensjon.testdata.repository;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PrimaryKeySwapper {

    public static String swapPrimaryKeysInSql(String sql) {
        Set<String> oldPrimaryKeys = getPrimaryKeys(sql);
        List<String> newPrimaryKeys =
                oldPrimaryKeys
                        .stream()
                        .map(PrimaryKeySwapper::generateNewPrimaryKey)
                        .collect(Collectors.toList());

        return StringUtils.replaceEach(sql, oldPrimaryKeys.toArray(new String[0]), newPrimaryKeys.toArray(new String[0]));
    }

    private static Set<String> getPrimaryKeys(String sql) {
        Set<String> oldPrimaryKeys = new HashSet<>();
        Matcher m = Pattern.compile("'\\d{8}'").matcher(sql);
        while (m.find()) {
            String group = m.group().replace("'","");
            oldPrimaryKeys.add(group);
        }
        return oldPrimaryKeys;
    }

    private static String generateNewPrimaryKey(String oldPrimaryKey) {
        Integer newPrimaryKey = Integer.valueOf(oldPrimaryKey) +  (new Random()).nextInt(50000000);
        return newPrimaryKey.toString();
    }
}
