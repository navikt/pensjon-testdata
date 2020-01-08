package no.nav.pensjon.testdata.configuration;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

@Configuration
public class DatasourceConfig {

    @Bean
    public DataSource getDatasource() throws SQLException, IOException {
        String dbUrl = SecretUtil.readSecret("db/jdbc_url");
        String username = SecretUtil.readSecret("oracle/username");
        String password = SecretUtil.readSecret("oracle/password");

        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("oracle.jdbc.OracleDriver");
        dataSourceBuilder.url(dbUrl);
        dataSourceBuilder.username(username);
        dataSourceBuilder.password(password);

        DataSource dataSource = dataSourceBuilder.build();


        return dataSource;
    }
}
