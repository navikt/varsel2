package no.nav.modig.security.tilgangskontroll.policy.pep;

import no.nav.modig.security.tilgangskontroll.policy.request.attributes.PolicyAttribute;
import no.nav.modig.security.tilgangskontroll.utils.AttributeUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.actionId;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.resourceId;
import static no.nav.modig.security.tilgangskontroll.utils.RequestUtils.forRequest;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Aspect
public class AccessControlInterceptor {
    @Autowired
    private EnforcementPoint pep;

    @Before("@annotation(accessControl)")
    public void enforcementpoint(JoinPoint jointPoint, AccessControl accessControl) {
        final List<PolicyAttribute> policyAttributes = new ArrayList<PolicyAttribute>();
        for (AccessControlAttribute attr : accessControl.attributes()) {
            policyAttributes.add(createPolicyAttribute(attr.name(), attr.value(), attr.type()));
        }

        // deprecated
        if (isNotBlank(accessControl.actionId())) {
            policyAttributes.add(actionId(accessControl.actionId()));
        }

        // deprecated
        if (isNotBlank(accessControl.resourceId())) {
            policyAttributes.add(resourceId(accessControl.resourceId()));
        }

        // process argument annotations
        Object[] params = jointPoint.getArgs();
        Method method = ((MethodSignature) jointPoint.getSignature()).getMethod();
        Annotation[][] paramAnnotations = method.getParameterAnnotations();

        populatePolicyAttributes(policyAttributes, params, paramAnnotations);

        pep.assertAccess(forRequest(policyAttributes.toArray(new PolicyAttribute[policyAttributes.size()])));

    }

    private void populatePolicyAttributes(List<PolicyAttribute> policyAttributes, Object[] params, Annotation[][] paramAnnotations) {
        for (int i = 0; i < paramAnnotations.length; i++) {
            if (paramAnnotations[i] != null) {
                String attrValue = params[i] == null ? null : params[i].toString();
                for (int j = 0; j < paramAnnotations[i].length; j++) {
                    extractAttributesFromAnnotation(policyAttributes, paramAnnotations[i][j], attrValue);
                }
            }
        }
    }

    private void extractAttributesFromAnnotation(List<PolicyAttribute> policyAttributes, Annotation annotation, String attrValue) {
        if (annotation instanceof AccessControlAttribute) {
            AccessControlAttribute attr = (AccessControlAttribute) annotation;
            policyAttributes.add(createPolicyAttribute(attr.name(), attrValue, attr.type()));
        }
    }

    private PolicyAttribute createPolicyAttribute(String name, String value,
            AttributeType type) {
        switch (type) {
        case SUBJECT:
            return AttributeUtils.subjectAttribute(name, value);
        case ACTION:
            return AttributeUtils.actionAttribute(name, value);
        case RESOURCE:
            return AttributeUtils.resourceAttribute(name, value);
        case ENVIRONMENT:
            return AttributeUtils.environmentAttribute(name, value);
        default:
            throw new IllegalArgumentException("Invalid attribute type: " + type);
        }
    }
}
