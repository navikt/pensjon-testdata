package no.nav.pensjon.testdata.configuration;



import no.nav.pensjon.testdata.configuration.support.BasicAuthSoapSecurityHandler;
import no.nav.pensjon.testdata.configuration.support.JaxWsConsumerProxyFactoryBean;
import no.nav.tjeneste.domene.pensjon.behandleautomatiskomregning.v1.binding.BehandleAutomatiskOmregningV1;
import no.nav.tjeneste.domene.pensjon.vedtaksbrev.binding.Vedtaksbrev;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.xml.ws.handler.HandlerResolver;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class SoapConfig {

    @Value("${pen.vedtaksbrev.endpoint}")
    private String vedtaksbrevEndpoint;

    @Value("${pen.auto.omregning.endpoint}")
    private String automatiskOmregningEndpoint;

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
                        new BasicAuthSoapSecurityHandler(username, password)
                ).collect(Collectors.toList()))
                .getObject();
    }


    @Bean
    public BehandleAutomatiskOmregningV1 behandleAutomatiskOmregning() throws IOException {
        String username = SecretUtil.readSecret("srvpensjon/username");
        String password = SecretUtil.readSecret("srvpensjon/password");

        return new JaxWsConsumerProxyFactoryBean<BehandleAutomatiskOmregningV1>()
                .wsdlDocumentUrl(new ClassPathResource("wsdl/no/nav/tjeneste/domene/pensjon/behandleAutomatiskOmregning/v1/Binding.wsdl"))
                .namespaceUri("http://nav.no/tjeneste/domene/pensjon/behandleAutomatiskOmregning/v1/Binding")
                .serviceInterface(BehandleAutomatiskOmregningV1.class)
                .serviceName("BehandleAutomatiskOmregning_v1")
                .portName("BehandleAutomatiskOmregning_v1Port")
                .endpointAddress(automatiskOmregningEndpoint)
                .handlerResolver(portInfo -> Stream.of(
                        new BasicAuthSoapSecurityHandler(username, password)
                ).collect(Collectors.toList()))
                .getObject();
    }

}
