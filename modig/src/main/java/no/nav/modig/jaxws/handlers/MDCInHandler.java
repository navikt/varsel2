package no.nav.modig.jaxws.handlers;

import no.nav.modig.common.MDCOperations;
import no.nav.modig.core.context.SubjectHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This handler extracts the callId from the SOAPHeader and puts it in the MDC
 * 
 */
public class MDCInHandler implements SOAPHandler<SOAPMessageContext> {
    protected static final Logger log = LoggerFactory.getLogger(MDCInHandler.class.getName());

    // QName for the callId header
    private static final QName CALLID_QNAME = new QName("uri:no.nav.applikasjonsrammeverk", MDCOperations.MDC_CALL_ID);

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        Boolean outbound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        // INBOUND processing
        if (!outbound) {
            log.debug("About to extract callId from SOAP message");
            SOAPHeader header = null;
            try {
                header = context.getMessage().getSOAPHeader();
            } catch (SOAPException e) {
                log.error(e.getMessage());
                throw new ProtocolException(e);
            }
            String callId = extractCallId(header);

            SubjectHandler subjectHandler = SubjectHandler.getSubjectHandler();

            String userId = subjectHandler.getUid() != null ? subjectHandler.getUid() : "";
            String consumerId = subjectHandler.getConsumerId() != null ? subjectHandler.getConsumerId() : "";

            MDCOperations.putToMDC(MDCOperations.MDC_CALL_ID, callId);
            MDCOperations.putToMDC(MDCOperations.MDC_USER_ID, userId);
            MDCOperations.putToMDC(MDCOperations.MDC_CONSUMER_ID, consumerId);
            log.debug("Values added");
        }
        return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        return true;
    }

    @Override
    public void close(MessageContext context) {
        MDCOperations.remove(MDCOperations.MDC_CALL_ID);
        MDCOperations.remove(MDCOperations.MDC_USER_ID);
        MDCOperations.remove(MDCOperations.MDC_CONSUMER_ID);
        log.debug("Cleared MDC session");
    }

    @Override
    public Set<QName> getHeaders() {
        log.debug("CallIdHandler - getHeaders ");

        return new HashSet<QName>() {
            {
                add(CALLID_QNAME);
            }
        };
    }

    private String extractCallId(SOAPHeader header) {
        String callId = "";

        if (header == null) {
            return callId;
        }

        Iterator<Node> headersIter = header.getChildElements(CALLID_QNAME);

        while (headersIter.hasNext()) {
            SOAPElement element = (SOAPElement) headersIter.next();
            log.debug("QName: " + element.getElementQName());
            if (element.getElementQName().equals(CALLID_QNAME)) {
                callId = element.getValue();
                log.debug("Found callId: " + callId);
                break;
            }
        }
        return callId;
    }

}
