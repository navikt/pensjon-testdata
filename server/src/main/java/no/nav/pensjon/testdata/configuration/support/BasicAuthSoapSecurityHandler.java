package no.nav.pensjon.testdata.configuration.support;


import no.nav.pensjon.testdata.configuration.support.SecurityHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.Collections;
import java.util.Set;

public class BasicAuthSoapSecurityHandler implements SOAPHandler<SOAPMessageContext> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final QName SECURITY_QNAME = new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security");
    private static final Set<QName> PROCESSED_HEADERS_QNAME = Collections.unmodifiableSet(Collections.singleton(SECURITY_QNAME));

    private final String serviceUsername;
    private final String servicePassword;

    public BasicAuthSoapSecurityHandler(String serviceUsername, String servicePassword) {
        this.serviceUsername = serviceUsername;
        this.servicePassword = servicePassword;
    }

    @Override
    public Set<QName> getHeaders() {
        return PROCESSED_HEADERS_QNAME;
    }

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        Boolean outbound = (Boolean) context.get(SOAPMessageContext.MESSAGE_OUTBOUND_PROPERTY);
        if (outbound) {
            if (logger.isTraceEnabled()) {
                QName serviceRef = (QName) context.get(SOAPMessageContext.WSDL_SERVICE);
                logger.trace("Handling message... [service: {}]", serviceRef.getLocalPart());
            }
            try {
                SOAPEnvelope se = context.getMessage().getSOAPPart().getEnvelope();
                SOAPHeader sh = se.getHeader();
                SOAPElement basicAuthHeader = createSecurityHeader();
                if (basicAuthHeader == null) {
                    throw new IllegalArgumentException("Could not create security header!");
                }
                sh.addChildElement(basicAuthHeader);
            } catch (SOAPException ex) {
                throw new IllegalArgumentException("Error while adding security header " + ex.getMessage(), ex);
            }
        }
        return true;
    }

    protected SOAPElement createSecurityHeader() {
        return SecurityHeader.createBasicAuth(serviceUsername, servicePassword);
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        return true;
    }

    @Override
    public void close(MessageContext context) {
        // NO-OP
    }
}
