package no.nav.pensjon.testdata.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.support.DatabaseStartupValidator;

import javax.sql.DataSource;
import java.io.IOException;


@Configuration
public class DatasourceConfig {
    private static final int DB_VALIDATION_INTERVAL_SECONDS = 10;

    Logger logger = LoggerFactory.getLogger(DatasourceConfig.class);

    @Primary
    @Bean
    @ConditionalOnProperty(
            value="pen.db.enabled",
            havingValue = "true")
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
        logDBDetails(dbUrl, username);
        return dataSourceBuilder.build();
    }

    @Bean
    @ConditionalOnProperty(
            value="pen.db.enabled",
            havingValue = "true")
    public DatabaseStartupValidator penDatabaseStartupValidator(DataSource dataSource, @Value("${db.startup.wait.seconds}") int timeout) {
        DatabaseStartupValidator databaseStartupValidator = new DatabaseStartupValidator();
        databaseStartupValidator.setDataSource(dataSource);
        databaseStartupValidator.setValidationQuery("SELECT 1 FROM PEN.T_PERSON");
        databaseStartupValidator.setInterval(DB_VALIDATION_INTERVAL_SECONDS);
        databaseStartupValidator.setTimeout(timeout);
        return databaseStartupValidator;
    }

    @Bean(name = "popp-datasource")
    @ConditionalOnProperty(
            value="popp.db.enabled",
            havingValue = "true")
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

        logDBDetails(dbUrl, username);
        return dataSourceBuilder.build();
    }

    @Bean
    @ConditionalOnProperty(
            value="popp.db.enabled",
            havingValue = "true")
    public DatabaseStartupValidator poppDatabaseStartupValidator(@Qualifier("popp-datasource") DataSource dataSource, @Value("${db.startup.wait.seconds}") int timeout) {
        DatabaseStartupValidator databaseStartupValidator = new DatabaseStartupValidator();
        databaseStartupValidator.setDataSource(dataSource);
        databaseStartupValidator.setValidationQuery("SELECT 1 FROM POPP.T_PERSON");
        databaseStartupValidator.setInterval(DB_VALIDATION_INTERVAL_SECONDS);
        databaseStartupValidator.setTimeout(timeout);
        return databaseStartupValidator;
    }

    @Bean(name = "sam-datasource")
    @ConditionalOnProperty(
            value="sam.db.enabled",
            havingValue = "true")
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

        logDBDetails(dbUrl, username);
        return dataSourceBuilder.build();
    }

    @Bean
    @ConditionalOnProperty(
            value="sam.db.enabled",
            havingValue = "true")
    public DatabaseStartupValidator samDatabaseStartupValidator(@Qualifier("sam-datasource") DataSource dataSource, @Value("${db.startup.wait.seconds}") int timeout) {
        DatabaseStartupValidator databaseStartupValidator = new DatabaseStartupValidator();
        databaseStartupValidator.setDataSource(dataSource);
        databaseStartupValidator.setValidationQuery("SELECT 1 FROM SAM.T_PERSON");
        databaseStartupValidator.setInterval(DB_VALIDATION_INTERVAL_SECONDS);
        databaseStartupValidator.setTimeout(timeout);
        return databaseStartupValidator;
    }

    @Bean(name = "moog-datasource")
    @ConditionalOnProperty(
            value="moog.db.enabled",
            havingValue = "true")
    public DataSource getMoogDatasource() throws IOException {
        logger.info("Creating datasource for Moog");
        String dbUrl = SecretUtil.readSecret("app/moog_jdbc_url");
        String username = SecretUtil.readSecret("app/moog_username");
        String password = SecretUtil.readSecret("app/moog_password");

        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("oracle.jdbc.OracleDriver");
        dataSourceBuilder.url(dbUrl);
        dataSourceBuilder.username(username);
        dataSourceBuilder.password(password);

        logDBDetails(dbUrl, username);
        return dataSourceBuilder.build();
    }

    private void logDBDetails(String dbUrl, String username) {
        logger.info("url=" + dbUrl + ", username=" + username);
    }
}
