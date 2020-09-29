package no.nav.pensjon.testdata.configuration;


import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.ws.handler.Handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import no.nav.pensjon.testdata.configuration.support.BasicAuthSoapSecurityHandler;
import no.nav.pensjon.testdata.configuration.support.JaxWsConsumerProxyFactoryBean;
import no.nav.pensjon.testdata.configuration.support.SAMLSoapSecurityHandler;
import no.nav.pensjon.testdata.configuration.support.StelvioContextHandler;
import no.nav.tjeneste.domene.pensjon.behandleautomatiskomregning.v1.binding.BehandleAutomatiskOmregningV1;
import no.nav.tjeneste.domene.pensjon.vedtaksbrev.binding.Vedtaksbrev;

@Configuration
public class SoapConfig {

    @Value("${pen.vedtaksbrev.endpoint}")
    private String vedtaksbrevEndpoint;

    @Value("${pen.auto.omregning.endpoint}")
    private String automatiskOmregningEndpoint;

    @Autowired
    private SAMLSoapSecurityHandler samlSoapSecurityHandler;

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
                .handlerResolver(portInfo -> handlers(vedtaksbrevEndpoint, username, password))
                .getObject();
    }

    private List<Handler> handlers(String vedtaksbrevEndpoint, String username, String password){
        if (vedtaksbrevEndpoint.contains("was")){
            return Arrays.asList(new BasicAuthSoapSecurityHandler(username, password), new StelvioContextHandler());
        } else{
            return Collections.singletonList(samlSoapSecurityHandler);
        }
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
                .handlerResolver(portInfo -> handlers(vedtaksbrevEndpoint, username, password))
                .getObject();
    }

}
