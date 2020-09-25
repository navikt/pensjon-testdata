package no.nav.pensjon.testdata.configuration.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.Set;

@Service
public class SAMLSoapSecurityHandler implements SOAPHandler<SOAPMessageContext> {
    private static final Logger LOG = LoggerFactory.getLogger(SAMLSoapSecurityHandler.class);

    @Autowired
    private SAMLTokenProvider samlTokenProvider;

    @Override
    public Set<QName> getHeaders() {
        return Collections.unmodifiableSet(Collections.singleton(new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security")));
    }

    @Override
    public boolean handleMessage(SOAPMessageContext context) {

        Boolean outbound = (Boolean) context.get(SOAPMessageContext.MESSAGE_OUTBOUND_PROPERTY);
        if (outbound) {
            if (LOG.isTraceEnabled()) {
                QName serviceRef = (QName) context.get(SOAPMessageContext.WSDL_SERVICE);
                LOG.trace("Handling OIDC->SAML... [service: {}]", serviceRef.getLocalPart());
            }

            try {
                SOAPEnvelope envelope = context.getMessage().getSOAPPart().getEnvelope();
                SOAPHeader header = envelope.getHeader();
                if (header == null) {
                    header = envelope.addHeader();
                }

                SOAPElement secHeader = generateSecurityHeaderWithSAML(samlTokenProvider.fetchSamlToken());
                Name soapenvName = SOAPFactory.newInstance().createName("mustUnderstand", "soapenv", "http://www.w3.org/2003/05/soap-envelope");
                secHeader.addAttribute(soapenvName, "1");
                header.addChildElement(secHeader);
            } catch (SOAPException e) {
                LOG.error("Error with the SOAP envelope/header!", e);
                throw new SecurityException(e);
            }
        }
        return true;
    }

    private SOAPElement generateSecurityHeaderWithSAML(SAMLResponse samlToken) {
        SOAPElement secHeader;
        try {
            LOG.debug("Token expires: {}", samlToken.getExpiresAt());
            SOAPFactory sFactory = SOAPFactory.newInstance();

            Name headerName = sFactory.createName("Security", "wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
            secHeader = sFactory.createElement(headerName);
            Document assertion = convertStringToDocument(samlToken.getDecodedToken());
            Document securityDoc = secHeader.getOwnerDocument();
            Node samlNode = securityDoc.importNode(assertion.getFirstChild(), true);
            secHeader.appendChild(samlNode);
        } catch (Exception e) {
            LOG.error("Error generating security header", e);
            throw new IllegalArgumentException("Error generating Security-element with SAML", e);
        }
        return secHeader;
    }

    private static Document convertStringToDocument(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        try (StringReader input = new StringReader(xmlStr)) {
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(input));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new IllegalArgumentException("Error parsing SAML assertion", e);
        }
    }

    @Override
    public boolean handleFault(SOAPMessageContext soapMessageContext) {
        return false;
    }

    @Override
    public void close(MessageContext messageContext) {

    }
}
