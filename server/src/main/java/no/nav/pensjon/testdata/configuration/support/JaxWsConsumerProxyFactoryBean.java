package no.nav.pensjon.testdata.configuration.support;

import org.apache.commons.lang3.Validate;
import org.springframework.core.io.Resource;
import org.springframework.remoting.jaxws.JaxWsPortProxyFactoryBean;

import javax.xml.ws.handler.HandlerResolver;
import java.io.IOException;

public class JaxWsConsumerProxyFactoryBean<T> {
    private Resource url;
    private String namespaceUri;
    private Class<?> serviceInterface;
    private String serviceName;
    private String portName;
    private String endpointAddress;
    private HandlerResolver handlerResolver;

    public JaxWsConsumerProxyFactoryBean<T> wsdlDocumentUrl(Resource url) {
        this.url = url;
        return this;
    }

    public JaxWsConsumerProxyFactoryBean<T> namespaceUri(String namespaceUri) {
        this.namespaceUri = namespaceUri;
        return this;
    }

    public JaxWsConsumerProxyFactoryBean<T> serviceInterface(Class<?> serviceInterface) {
        this.serviceInterface = serviceInterface;
        return this;
    }

    public JaxWsConsumerProxyFactoryBean<T> serviceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public JaxWsConsumerProxyFactoryBean<T> portName(String portName) {
        this.portName = portName;
        return this;
    }

    public JaxWsConsumerProxyFactoryBean<T> endpointAddress(String endpointAddress) {
        this.endpointAddress = endpointAddress;
        return this;
    }

    public JaxWsConsumerProxyFactoryBean<T> handlerResolver(HandlerResolver handlerResolver) {
        this.handlerResolver = handlerResolver;
        return this;
    }

    @SuppressWarnings("unchecked")
    public T getObject() {
        Validate.notNull(url, "URL not set");
        Validate.notNull(namespaceUri, "namespaceUri not set");
        Validate.notNull(serviceInterface, "serviceInterface not set");
        Validate.notNull(serviceName, "serviceName not set");
        Validate.notNull(portName, "portName not set");
        Validate.notNull(endpointAddress, "endpointAddress not set");
        Validate.notNull(handlerResolver, "handlerResolver not set");

        JaxWsPortProxyFactoryBean portFactoryBean = new JaxWsPortProxyFactoryBean();
        try {
            portFactoryBean.setWsdlDocumentUrl(url.getURL());
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        portFactoryBean.setNamespaceUri(namespaceUri);
        portFactoryBean.setServiceInterface(serviceInterface);
        portFactoryBean.setServiceName(serviceName);
        portFactoryBean.setPortName(portName);
        portFactoryBean.setEndpointAddress(endpointAddress);
        portFactoryBean.setHandlerResolver(handlerResolver);

        portFactoryBean.afterPropertiesSet();
        return (T) portFactoryBean.getObject();
    }
}
