package no.nav.pensjon.testdata.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class JdbcTemplateConfig {

    Logger logger = LoggerFactory.getLogger(JdbcTemplateConfig.class);

    @Primary
    @Bean
    public JdbcTemplate penJdbcTemplate(DataSource ds) {
        logger.info("Creating PEN jdbcTemplate");
        return new JdbcTemplate(ds);
    }

    @Bean(name = "poppJdbcTemplate")
    public JdbcTemplate poppJdbcTemplate(@Qualifier("popp-datasource") DataSource ds) {
        logger.info("Creating POPP jdbcTemplate");
        return new JdbcTemplate(ds);
    }

    @Bean(name = "samJdbcTemplate")
    public JdbcTemplate samJdbcTemplate(@Qualifier("sam-datasource") DataSource ds) {
        logger.info("Creating SAM jdbcTemplate");
        return new JdbcTemplate(ds);
    }
}
