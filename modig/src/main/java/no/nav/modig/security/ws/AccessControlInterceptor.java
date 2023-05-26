package no.nav.modig.security.ws;

import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import org.apache.cxf.binding.soap.SoapFault;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.phase.PhaseInterceptorChain;

import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.actionId;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.resourceId;
import static no.nav.modig.security.tilgangskontroll.utils.RequestUtils.forRequest;

/**
 * This interceptor uses the supplied {@link EnforcementPoint} to check if access is granted for the specified operation. The
 * result is stored as a property in {@link SoapMessage} with the key {@value #PEP_ACCESS_GRANTED}.
 */
public class AccessControlInterceptor extends AbstractPhaseInterceptor<SoapMessage> {
    public static final String PEP_ACCESS_GRANTED = AccessControlInterceptor.class.getName() + ".PEP_ACCESS_GRANTED";

    private EnforcementPoint pep;

    public AccessControlInterceptor() {
        super(Phase.PRE_INVOKE);
    }

    public void setEnforcementPoint(EnforcementPoint pep) {
        this.pep = pep;
    }

    @Override
    public void handleMessage(SoapMessage message) {
        try {
            final Exchange exchange = message.getExchange();
            final String serviceName = exchange.getService().getName().getLocalPart();
            final String operationName = exchange.getBindingOperationInfo().getName().getLocalPart();
            final Boolean hasAccess = pep.hasAccess(forRequest(resourceId(serviceName), actionId(operationName)));

            // TODO: should the interceptor handle the "access denied" cases?
            message.put(PEP_ACCESS_GRANTED, hasAccess);
        } catch (Exception e) {
            throw new SoapFault("Access control failed", e, message.getVersion().getSender());
        }
    }

    public static boolean accessGranted() {
        final Message message = PhaseInterceptorChain.getCurrentMessage();

        if (message == null) {
            throw new IllegalStateException("No current message available!");
        }

        final Boolean accessGranted = (Boolean) message.get(PEP_ACCESS_GRANTED);
        return accessGranted != null && accessGranted;
    }
}
