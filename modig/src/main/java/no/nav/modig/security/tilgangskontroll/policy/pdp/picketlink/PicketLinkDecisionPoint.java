package no.nav.modig.security.tilgangskontroll.policy.pdp.picketlink;

import no.nav.modig.security.tilgangskontroll.policy.pdp.DecisionPoint;
import no.nav.modig.security.tilgangskontroll.policy.request.PolicyRequest;
import no.nav.modig.security.tilgangskontroll.policy.response.Decision;
import no.nav.modig.security.tilgangskontroll.policy.response.PolicyResponse;
import org.jboss.security.xacml.core.JBossPDP;
import org.jboss.security.xacml.interfaces.PolicyDecisionPoint;
import org.jboss.security.xacml.interfaces.RequestContext;
import org.jboss.security.xacml.interfaces.ResponseContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

/**
 * Decision point that uses PicketLink XACML as implementation.
 */
public class PicketLinkDecisionPoint implements DecisionPoint {
    private static final Logger LOG = LoggerFactory.getLogger(PicketLinkDecisionPoint.class);

    private final PolicyDecisionPoint pdp;

    /**
     * Resolves JBOSS issue https://issues.jboss.org/browse/SECURITY-742 by setting pdp lock strategy to "lockfree"
     */
    public PicketLinkDecisionPoint(URL configUrl) {
        setLockfree(true);
        this.pdp = new JBossPDP(configUrl);

    }

     private void setLockfree(boolean lockfree){
         if (lockfree){
             System.setProperty("picketbox.xacml.pdp.lockstrategy", "lockfree");
         } else{
             System.setProperty("picketbox.xacml.pdp.lockstrategy", "");
         }
     }

    @Override
    public PolicyResponse evaluate(PolicyRequest request) {
        RequestContext xacmlRequest = RequestTypeFactory.createXacmlRequest(request);

        if (LOG.isTraceEnabled()) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                xacmlRequest.marshall(os);
                LOG.trace("XACML REQUEST: {},", os);
            } catch (IOException e) {
                LOG.warn("Failed to marshal xacml request.", e);
            }
        }

        ResponseContext decision = pdp.evaluate(xacmlRequest);

        if (LOG.isTraceEnabled()) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                decision.marshall(os);
                LOG.trace("XACML RESPONSES: {},", os);
            } catch (IOException e) {
                LOG.warn("Failed to marshal xacml response.", e);
            }
        }

        return new PolicyResponse(Decision.getInstanceFromCodeValue(decision.getDecision()));
    }
}
