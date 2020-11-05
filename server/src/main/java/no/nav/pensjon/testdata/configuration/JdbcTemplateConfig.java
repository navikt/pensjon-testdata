package no.nav.pensjon.testdata.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class JdbcTemplateConfig {

    Logger logger = LoggerFactory.getLogger(JdbcTemplateConfig.class);

    @Primary
    @Bean(name = "penJdbcTemplate")
    @ConditionalOnProperty(
            value="pen.db.enabled",
            havingValue = "true")
    @DependsOn("penDatabaseStartupValidator")
    public JdbcTemplate penJdbcTemplate(DataSource ds) {
        logger.info("Connected to PEN");
        return new JdbcTemplate(ds);
    }

    @Bean(name = "poppJdbcTemplate")
    @ConditionalOnProperty(
            value="popp.db.enabled",
            havingValue = "true")
    @DependsOn("poppDatabaseStartupValidator")
    public JdbcTemplate poppJdbcTemplate(@Qualifier("popp-datasource") DataSource ds) {
        logger.info("Connected to POPP");
        return new JdbcTemplate(ds);
    }

    @Bean(name = "samJdbcTemplate")
    @ConditionalOnProperty(
            value="sam.db.enabled",
            havingValue = "true")
    @DependsOn("samDatabaseStartupValidator")
    public JdbcTemplate samJdbcTemplate(@Qualifier("sam-datasource") DataSource ds) {
        logger.info("Connected to SAM");
        return new JdbcTemplate(ds);
    }
}
