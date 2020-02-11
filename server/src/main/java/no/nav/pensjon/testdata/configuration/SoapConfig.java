package no.nav.pensjon.testdata.configuration;



import no.nav.pensjon.testdata.configuration.support.BasicAuthSoapSecurityHandler;
import no.nav.pensjon.testdata.configuration.support.JaxWsConsumerProxyFactoryBean;
import no.nav.tjeneste.domene.pensjon.vedtaksbrev.binding.Vedtaksbrev;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class SoapConfig {

    @Value("${pen.vedtaksbrev.endpoint}")
    private String vedtaksbrevEndpoint;

    @Bean
    public Vedtaksbrev bestillAutomatiskBrev() throws IOException {
        String username = SecretUtil.readSecret("srvpensjon/username");
        String password = SecretUtil.readSecret("srvpensjon/password");

        return new JaxWsConsumerProxyFactoryBean<Vedtaksbrev>()
                .wsdlDocumentUrl(new ClassPathResource("/wsdl/no/nav/tjeneste/domene/pensjon/vedtaksbrev/Binding.wsdl"))
                .namespaceUri("http://nav.no/tjeneste/domene/pensjon/vedtaksbrev/Binding")
                .portName("VedtaksbrevPort")
                .serviceName("Vedtaksbrev")
                .serviceInterface(Vedtaksbrev.class)
                .endpointAddress(vedtaksbrevEndpoint)
                .handlerResolver(portInfo -> Stream.of(
                        new BasicAuthSoapSecurityHandler(username, password)  // Order: Don't include security token in log output
                ).collect(Collectors.toList()))
                .getObject();
    }



}
