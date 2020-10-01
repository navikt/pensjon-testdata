package no.nav.pensjon.testdata.configuration;

import no.nav.pensjon.testdata.configuration.support.JdbcTemplateWrapper;
import no.nav.pensjon.testdata.repository.support.ComponentCode;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
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

    @Bean
    public ApplicationRunner initialize(JdbcTemplateWrapper wrapper){
        return args -> {
            initDBConnection(wrapper, ComponentCode.PEN, "select 1 from dual");
            initDBConnection(wrapper, ComponentCode.POPP, "select 1 from dual");
            initDBConnection(wrapper, ComponentCode.SAM, "select 1 from dual");
        };
    }

    private void initDBConnection(JdbcTemplateWrapper wrapper, ComponentCode code, String sql) {
        try{
            wrapper.execute(code, sql);
        }
        catch (Exception e){
            LoggerFactory.getLogger("JdbcWrapperConfig").error("Could not run init query on " + code + " db", e);
        }
    }
}
