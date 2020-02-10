package no.nav.pensjon.testdata.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.io.IOException;


@Configuration
public class DatasourceConfig {

    Logger logger = LoggerFactory.getLogger(DatasourceConfig.class);

    @Primary
    @Bean
    public DataSource getDatasource() throws IOException {
        logger.info("Creating datasource for PEN");
        String dbUrl = SecretUtil.readSecret("db/pen/jdbc_url");
        String username = SecretUtil.readSecret("oracle/pen/username");
        String password = SecretUtil.readSecret("oracle/pen/password");

        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("oracle.jdbc.OracleDriver");
        dataSourceBuilder.url(dbUrl);
        dataSourceBuilder.username(username);
        dataSourceBuilder.password(password);

        return dataSourceBuilder.build();
    }

    @Bean(name = "popp-datasource")
    public DataSource getPOPPDatasource() throws IOException {
        logger.info("Creating datasource for POPP");
        String dbUrl = SecretUtil.readSecret("db/popp/jdbc_url");
        String username = SecretUtil.readSecret("oracle/popp/username");
        String password = SecretUtil.readSecret("oracle/popp/password");

        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("oracle.jdbc.OracleDriver");
        dataSourceBuilder.url(dbUrl);
        dataSourceBuilder.username(username);
        dataSourceBuilder.password(password);

        return dataSourceBuilder.build();
    }

    @Bean(name = "sam-datasource")
    public DataSource getSAMDatasource() throws IOException {
        logger.info("Creating datasource for SAM");
        String dbUrl = SecretUtil.readSecret("db/sam/jdbc_url");
        String username = SecretUtil.readSecret("oracle/sam/username");
        String password = SecretUtil.readSecret("oracle/sam/password");

        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("oracle.jdbc.OracleDriver");
        dataSourceBuilder.url(dbUrl);
        dataSourceBuilder.username(username);
        dataSourceBuilder.password(password);

        return dataSourceBuilder.build();
    }
}
