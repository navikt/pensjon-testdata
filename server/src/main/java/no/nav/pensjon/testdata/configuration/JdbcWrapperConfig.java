package no.nav.pensjon.testdata.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import no.nav.pensjon.testdata.configuration.support.JdbcTemplateWrapper;
import no.nav.pensjon.testdata.repository.support.ComponentCode;

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

    @Bean
    public ApplicationRunner initialize(JdbcTemplateWrapper wrapper){
        return args -> {
            wrapper.execute(ComponentCode.PEN, "SELECT 1 FROM PEN.T_PERSON");
            wrapper.execute(ComponentCode.POPP, "SELECT 1 FROM POPP.T_PERSON");
            wrapper.execute(ComponentCode.SAM, "SELECT 1 FROM SAM.T_PERSON");
        };
    }
}
