package no.nav.modig.security.tilgangskontroll.policy.pip;

import no.nav.modig.security.tilgangskontroll.policy.webservice.DiskresjonskodeService;
import no.nav.modig.security.tilgangskontroll.policy.webservice.DiskresjonskodeServiceImpl;
import org.apache.commons.lang3.Validate;
import org.jboss.security.xacml.locators.attrib.StorageAttributeLocator;
import org.jboss.security.xacml.sunxacml.EvaluationCtx;
import org.jboss.security.xacml.sunxacml.attr.AttributeValue;
import org.jboss.security.xacml.sunxacml.attr.BagAttribute;
import org.jboss.security.xacml.sunxacml.attr.StringAttribute;
import org.jboss.security.xacml.sunxacml.cond.EvaluationResult;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang3.text.StrSubstitutor.replaceSystemProperties;

public class DiskresjonskodeLocator extends StorageAttributeLocator {

    private static final String STRING_TYPE_IDENTIFIER = "http://www.w3.org/2001/XMLSchema#string";
    private static final URI STRING_TYPE_URI = URI
            .create(STRING_TYPE_IDENTIFIER);
    private static final URI PERSON_FODELSELSNUMMER = URI
            .create("urn:nav:ikt:tilgangskontroll:xacml:resource:person:fodselsnummer");

    public static final String OPTION_URL = "url";
    private String wsUrl;

    protected DiskresjonskodeService diskresjonskodeService = new DiskresjonskodeServiceImpl();
    private LocatorDelegate delegate = new LocatorDelegate();

    @Override
    public EvaluationResult findAttribute(URI attributeType, URI attributeId,
            URI issuer, URI subjectCategory, EvaluationCtx context,
            int designatorType) {

        if (!ids.contains(attributeId)) {
            return delegate.createEmptyEvaluationResult(attributeType,
                    attributeId);
        }

        String fNr = getSubstituteValue(attributeType, context);

        String diskresjonskodeStr = "";
        try {
            diskresjonskodeStr = getDiskresjonskode(fNr);
        } catch (MalformedURLException e) {
            return delegate.createEmptyEvaluationResult(attributeType,
                    attributeId);
        }

        Set<AttributeValue> bagSet = new HashSet();
        AttributeValue diskresjonskodeAV = new StringAttribute(
                diskresjonskodeStr);
        bagSet.add(diskresjonskodeAV);

        return new EvaluationResult(new BagAttribute(attributeType, bagSet));
    }

    private String getDiskresjonskode(String resourceValue)
            throws MalformedURLException {
        return diskresjonskodeService.getDiskresjonskode(resourceValue, wsUrl);
    }

    @Override
    protected String getSubstituteValue(URI attributeType, EvaluationCtx context) {
        EvaluationResult evalResult = context.getResourceAttribute(STRING_TYPE_URI, PERSON_FODELSELSNUMMER, null);
        String fNr = (String) this.getAttributeValue(evalResult, attributeType);
        Validate.notBlank(fNr);
        return fNr;
    }

    @Override
    protected void usePassedOption(String optionTag, String optionValue) {
        super.usePassedOption(optionTag, optionValue);

        if (OPTION_URL.equalsIgnoreCase(optionTag)) {
            wsUrl = replaceSystemProperties(optionValue);
            Validate.notBlank(wsUrl, "URL til servicegateway er ikke satt.");
        }
    }
}
