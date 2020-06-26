package no.nav.pensjon.testdata.service;

import no.nav.pensjon.testdata.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class MoogService {

    @Autowired
    @Qualifier("moog-datasource")
    private Optional<DataSource> moogDataSource;

    @Autowired
    private FileRepository fileRepository;

    public List<String> execute(String fom, String tom, List<String> identer) throws SQLException, IOException {
        final DataSource dataSource = moogDataSource.orElseThrow(() -> new RuntimeException("Ikke tilgjengelig uten MOOG"));

        String sql = fileRepository
                .readSqlFileAsString("moog_db/fetch-testdata-log")
                .replace("{fom}", fom)
                .replace("{tom}",tom);
        Connection connection = dataSource.getConnection();
        alterSessionNlsParameters(connection);

        List<String> logg = fetchTestdataLog( sql, connection);
        List<String> result = fjernUonskedeIdenter(identer, logg);
        return result;
    }

    private void alterSessionNlsParameters(Connection connection) throws SQLException {
        connection.prepareStatement("alter session set nls_date_format='YYYY-MM-DD HH24:MI:SS'").execute();
        connection.prepareStatement("alter session set nls_timestamp_format='YYYY-MM-DD HH24:MI:SS'").execute();
        connection.prepareStatement("alter session set nls_numeric_characters=',.'").execute();
    }

    private List<String>  fetchTestdataLog(String sql, Connection connection) throws SQLException {
        List<String> result = new ArrayList<>();
        try (Connection c = connection;
             Statement s = c.createStatement()) {
            try {
                s.executeUpdate("begin dbms_output.enable(buffer_size => NULL); end;");
                s.execute(sql);

                try (CallableStatement call = c.prepareCall(
                        "declare "
                                + "  num integer := 10000;"
                                + "begin "
                                + "  dbms_output.get_lines(?, num);"
                                + "end;"
                )) {
                    call.registerOutParameter(1, Types.ARRAY,
                            "DBMSOUTPUT_LINESARRAY");
                    call.execute();

                    Array array = null;
                    try {
                        array = call.getArray(1);
                        result.addAll(Arrays.asList((String[]) array.getArray()));
                    }
                    finally {
                        if (array != null)
                            array.free();
                    }
                }
            }
            finally {
                s.execute("begin DBMS_LOGMNR.END_LOGMNR; end;");
                s.executeUpdate("begin dbms_output.disable(); end;");
            }
        }
        return result;
    }

    private List<String> fjernUonskedeIdenter(List<String> identer, List<String> logg) {
        List<String> result = new ArrayList<>();
        for (String query : logg) {
            if(identer == null || identer.isEmpty() || containsIdentity(query, identer)) {
                result.add(query);
            }
        }
        return result;
    }

    private boolean containsIdentity(String line, List<String> identer) {
        for (String ident : identer) {
            if (line.toLowerCase().contains(ident.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
