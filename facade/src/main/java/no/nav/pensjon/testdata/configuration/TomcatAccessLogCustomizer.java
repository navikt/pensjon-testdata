package no.nav.pensjon.testdata.configuration;

import ch.qos.logback.access.tomcat.LogbackValve;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

@Component
public class TomcatAccessLogCustomizer implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        final LogbackValve logbackValve = new LogbackValve();
        logbackValve.setName("Logback Access");
        logbackValve.setFilename("logback-access.xml");
        factory.addEngineValves(logbackValve);
    }
}