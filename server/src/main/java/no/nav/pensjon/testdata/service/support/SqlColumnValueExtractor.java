package no.nav.pensjon.testdata.service.support;

import java.util.Optional;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;

public interface SqlColumnValueExtractor {

     static Optional<String> extract(String query, String column){
         query = query.toUpperCase();
         column = column.toUpperCase();
         if (StringUtils.contains(query, column)) {
             if (StringUtils.contains(query, "VALUES")) {
                 return extractFromInsert(query, column);
             } else {
                 return extractFromUpdate(query, column);
             }
         }
         return Optional.empty();
    }

    private static Optional<String> extractFromUpdate(String query, String column) {
        return Optional.of(query)
                .map(sql -> StringUtils.substring(sql, StringUtils.indexOf(sql, column)))
                .map(sql -> StringUtils.substring(sql, 0, IntStream.of(StringUtils.indexOf(sql, ","), StringUtils.indexOf(sql, "AND"))
                        .filter(i -> i >= 0)
                        .min()
                        .orElseThrow()))
                .map(sql -> StringUtils.substring(sql, StringUtils.indexOf(sql, "=") + 1))
                .map(String::trim)
                .map(orgEnhetId -> StringUtils.strip(orgEnhetId, "'"))
                .filter(StringUtils::isNotBlank);
    }

    private static Optional<String> extractFromInsert(String query, String column) {
        String[] columnsList = Optional.of(query)
                .map(sql -> StringUtils.substring(sql, StringUtils.indexOf(query, "("), StringUtils.indexOf(query, "VALUES")))
                .map(String::trim)
                .map(sql -> StringUtils.removeEnd(sql, ")"))
                .map(sql -> StringUtils.removeStart(sql, "("))
                .map(sql -> StringUtils.split(sql, ","))
                .orElseThrow();

        String values = Optional.of(query)
                .map(sql -> StringUtils.substring(sql, StringUtils.indexOf(query, "VALUES") + 6, StringUtils.lastIndexOf(query, ")")))
                .map(String::trim)
                .map(sql -> StringUtils.removeEnd(sql, ")"))
                .map(sql -> StringUtils.removeStart(sql, "("))
                .orElseThrow();

        while (values.contains("(")) {
            int oracleFunctionStart = StringUtils.indexOf(values, "(");
            int oracleFunctionEnd = StringUtils.indexOf(values, ")") + 1;
            values = StringUtils.overlay(values, "_oracle_function", oracleFunctionStart, oracleFunctionEnd);
        }

        String[] valuesList = StringUtils.split(values, ",");

        return IntStream.range(0, columnsList.length)
                .filter(i -> StringUtils.contains(columnsList[i], column))
                .mapToObj(i -> valuesList[i])
                .map(orgEnhetId -> StringUtils.strip(orgEnhetId, "'"))
                .filter(StringUtils::isNotBlank)
                .findFirst();
    }

}
