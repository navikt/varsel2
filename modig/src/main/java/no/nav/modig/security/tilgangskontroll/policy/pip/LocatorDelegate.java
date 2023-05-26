package no.nav.modig.security.tilgangskontroll.policy.pip;

import org.jboss.security.xacml.sunxacml.attr.BagAttribute;
import org.jboss.security.xacml.sunxacml.cond.EvaluationResult;

import java.net.URI;

public class LocatorDelegate {
    protected EvaluationResult createEmptyEvaluationResult(URI attributeType, URI attributeId) {
        if (attributeType != null) {
            return new EvaluationResult(BagAttribute.createEmptyBag(attributeType));
        }
        return new EvaluationResult(BagAttribute.createEmptyBag(attributeId));
    }
}
