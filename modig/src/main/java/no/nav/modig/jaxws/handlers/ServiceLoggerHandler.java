package no.nav.modig.jaxws.handlers;

import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPFault;
import jakarta.xml.ws.handler.MessageContext;
import jakarta.xml.ws.handler.soap.SOAPHandler;
import jakarta.xml.ws.handler.soap.SOAPMessageContext;
import jakarta.xml.ws.soap.SOAPFaultException;
import org.slf4j.Logger;

import javax.xml.namespace.QName;
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
