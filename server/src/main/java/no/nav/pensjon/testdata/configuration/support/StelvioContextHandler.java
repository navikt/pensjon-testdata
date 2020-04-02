package no.nav.pensjon.testdata.configuration.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.transform.dom.DOMResult;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public class StelvioContextHandler implements SOAPHandler<SOAPMessageContext> {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    protected static final QName STELVIO_CONTEXT_QNAME = new QName("http://www.nav.no/StelvioContextPropagation", "StelvioContext");
    private static final Set<QName> PROCESSED_HEADERS_QNAME;
    private static final JAXBContext jaxbContext;

    public StelvioContextHandler() {
    }

    public boolean handleMessage(SOAPMessageContext context) {
        Boolean outbound = (Boolean)context.get("javax.xml.ws.handler.message.outbound");
        if (outbound) {
            StelvioContextData stelvioContextData = this.getCurrentStelvioContextData();

            try {
                SOAPEnvelope envelope = context.getMessage().getSOAPPart().getEnvelope();
                SOAPHeader header = envelope.getHeader();
                if (header == null) {
                    header = envelope.addHeader();
                }

                Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
                jaxbMarshaller.marshal(stelvioContextData, new DOMResult(header));
            } catch (SOAPException var7) {
                this.log.error("Errpr with the SOAP envelope/header: ", var7);
            } catch (JAXBException var8) {
                this.log.error("Error while marshalling the stelvioContextData element: ", var8);
            }
        }

        return true;
    }

    private StelvioContextData getCurrentStelvioContextData() {
        StelvioContextData stelvioContextData = new StelvioContextData();
        stelvioContextData.setApplicationId("Pensjon-testdata");
        stelvioContextData.setCorrelationId(UUID.randomUUID().toString());
        stelvioContextData.setUserId("Pensjon-testdata");
        return stelvioContextData;
    }

    public boolean handleFault(SOAPMessageContext context) {
        return true;
    }

    public void close(MessageContext context) {
    }

    public Set<QName> getHeaders() {
        return PROCESSED_HEADERS_QNAME;
    }

    static {
        PROCESSED_HEADERS_QNAME = Collections.singleton(STELVIO_CONTEXT_QNAME);

        try {
            jaxbContext = JAXBContext.newInstance(new Class[]{StelvioContextData.class});
        } catch (JAXBException var1) {
            throw new RuntimeException(var1);
        }
    }
}
