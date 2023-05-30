package no.nav.modig.jaxws.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.soap.SOAPFaultException;
import java.util.Set;

public class ServiceLoggerHandler implements SOAPHandler<SOAPMessageContext> {
    public Logger log;

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {

        Boolean outbound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        // OUTBOUND processing
        if (outbound) {

            Throwable exception;
            try {
                SOAPFault fault = context.getMessage().getSOAPBody().getFault();
                exception = fault == null ? null : new SOAPFaultException(fault);
                if (exception != null) {
                    log.error(exception.getMessage(), exception);
                }
            } catch (SOAPException e1) {
                log.error("handlefault problem", e1);
            }
        }
        // continue processing.
        return true;

    }

    @Override
    public void close(MessageContext context) {
    }

    @Override
    public Set<QName> getHeaders() {
        return null;
    }

}
