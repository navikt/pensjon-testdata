package no.nav.pensjon.testdata.util;


import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Kan brukes for å fjerne sql fra sql-uttrekk som gjør insert og deretter delete av samme data
 * f.e, fjerne :
 * insert into "PEN"."T_OPPTJN"("OPPTJN_ID","K_OPPTJN_T","PERSON_GRUNNLAG_ID","K_GRUNNLAG_KILDE","K_KILDE_POPP_T","AR","PI","PIA","PP","BRUK","MAKS_UFOREGRAD","REG_OPPRETTET_AV","REG_ENDRET_AV","DATO_OPPRETTET","OPPRETTET_AV","DATO_ENDRET","ENDRET_AV","VERSJON","EKSTERN_OPPTJN_ID","GRUNNLAG_VEDLEGG_ID") values ('1011949460','PPI','42276953','POPP','PEN','2001','243240','243240','3,81','1',NULL,'srvpensjon','srvpensjon',TO_TIMESTAMP('2020-08-07 11:43:38'),'z990380',TO_TIMESTAMP('2020-08-07 11:43:38'),'z990380','0','543619665','110045304');
 * delete from "PEN"."T_OPPTJN" where "OPPTJN_ID" = '1011949460' and "K_OPPTJN_T" = 'PPI' and "PERSON_GRUNNLAG_ID" = '42276953' and "K_GRUNNLAG_KILDE" = 'POPP' and "K_KILDE_POPP_T" = 'PEN' and "AR" = '2001' and "PI" = '243240' and "PIA" = '243240' and "PP" = '3,81' and "BRUK" = '1' and "MAKS_UFOREGRAD" IS NULL and "REG_OPPRETTET_AV" = 'srvpensjon' and "REG_ENDRET_AV" = 'srvpensjon' and "DATO_OPPRETTET" = TO_TIMESTAMP('2020-08-07 11:43:38') and "OPPRETTET_AV" = 'z990380' and "DATO_ENDRET" = TO_TIMESTAMP('2020-08-07 11:43:38') and "ENDRET_AV" = 'z990380' and "VERSJON" = '0' and "EKSTERN_OPPTJN_ID" = '543619665' and "GRUNNLAG_VEDLEGG_ID" = '110045304';
 */
public class RemoveDeletedItemsFromSQL {

    //Fjerner alle linjer med INSERT INTO ... VALUES ('ID', ...), hvis det finnes tilsvarende DELETE from ... where ID = 'ID'
    public static void main(String[] args) throws IOException {
        List<String> contents = Files.lines(Paths.get("/absolute/path/to/sql-uttrekk"), Charset.defaultCharset())
                .collect(Collectors.toList());

        List<String> deletedItemsIDs = contents.stream()
                .filter(s -> StringUtils.containsIgnoreCase(s, "delete from \"PEN\".\"T_OPPTJN\""))
                .map(s -> StringUtils.substringBefore(StringUtils.substringAfter(s, "'"), "'"))
                .collect(Collectors.toList());

        List<String> sqls = contents.stream()
                .filter(s -> insertShouldBeKept(s, deletedItemsIDs))
                .filter(s -> deleteShouldBeKept(s, "OPPTJN_ID", deletedItemsIDs))
                .collect(Collectors.toList());
        Path write = Files.write(Paths.get("absolute/path/to/result"), sqls, StandardOpenOption.CREATE_NEW);

        System.out.println(write);
    }

    private static boolean deleteShouldBeKept(String s, String columnName, List<String> deletedItemsIDs) {
        String sql = StringUtils.remove(s, " ").trim();
        return deletedItemsIDs.parallelStream()
                .filter(id -> StringUtils.containsIgnoreCase(sql, "\"" + columnName + "\"='" + id + "'")) // satser på ID-verdi er først i lista " VALUES ('1011949588', "
                .findAny()
                .isEmpty();
    }

    private static boolean insertShouldBeKept(String s, List<String> deletedItemsIDs) {
        String sql = StringUtils.remove(s, " ").trim();
        return deletedItemsIDs.parallelStream()
                .filter(id -> StringUtils.containsIgnoreCase(sql, "('" + id + "'")) // satser på ID-verdi er først i lista " VALUES ('1011949588', "
                .findAny()
                .isEmpty();
    }
}
