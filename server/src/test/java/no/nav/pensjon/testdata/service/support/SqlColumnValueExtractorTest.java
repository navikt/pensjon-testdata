package no.nav.pensjon.testdata.service.support;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class SqlColumnValueExtractorTest {

    @ParameterizedTest
    @MethodSource
    void extractColumnValueFromSql(Path path) throws IOException {
            List<String> sqls = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (String sql : sqls) {
                if (StringUtils.containsIgnoreCase(sql, "PEN_ORG_ENHET_ID")) {
                    Optional<String> pen_org_enhet_id = SqlColumnValueExtractor.extract(sql, "PEN_ORG_ENHET_ID");
                    Assert.assertTrue(pen_org_enhet_id.isPresent());
                }
            }
    }

    @SuppressWarnings("unused")
    private static Stream<Path> extractColumnValueFromSql() throws IOException  {
        return Files.find(Paths.get("sql/scenario/"),
                20,
                (filePath, fileAttr) -> filePath.endsWith("pen.sql"));
    }
}