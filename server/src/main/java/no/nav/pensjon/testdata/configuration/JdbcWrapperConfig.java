package no.nav.pensjon.testdata.configuration;

import no.nav.pensjon.testdata.configuration.support.JdbcTemplateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class JdbcWrapperConfig {

    @Qualifier("penJdbcTemplate")
    @Autowired(required = false)
    private JdbcTemplate jdbcTemplatePen;

    @Autowired(required = false)
    @Qualifier("poppJdbcTemplate")
    private JdbcTemplate jdbcTemplatePopp;

    @Autowired(required = false)
    @Qualifier("samJdbcTemplate")
    private JdbcTemplate jdbcTemplateSam;

    @Bean
    public JdbcTemplateWrapper getJdbcWrapper() {
        return new JdbcTemplateWrapper(jdbcTemplatePen,jdbcTemplatePopp,jdbcTemplateSam);
    }
}
