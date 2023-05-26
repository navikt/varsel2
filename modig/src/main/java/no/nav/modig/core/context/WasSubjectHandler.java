package no.nav.modig.core.context;

import com.ibm.websphere.security.WSSecurityException;
import com.ibm.websphere.security.auth.WSSubject;
import com.ibm.websphere.wssecurity.wssapi.token.SAMLToken;
import com.ibm.wsspi.wssecurity.saml.data.SAMLAttribute;
import no.nav.modig.core.domain.IdentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.xml.ws.ProtocolException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class WasSubjectHandler extends SubjectHandler {

    private static final Logger logger = LoggerFactory.getLogger(WasSubjectHandler.class);

    private static final String IDENT_TYPE = "identType";
    private static final String AUTHENTICATION_LEVEL = "authenticationLevel";
    private static final String CONSUMER_ID = "consumerId";

    @Override
    public Subject getSubject() {

        Subject subject;
        try {
            subject = WSSubject.getCallerSubject();
        } catch (WSSecurityException e) {
            logger.error("No subject on request");
            return null;
        }
        return subject;
    }

    @Override
    protected String getUidFromSAMLToken() {
        Subject subject = getSubject();
        SAMLToken samlToken = getSamlToken(subject);
        return samlToken.getSAMLNameID().getValue();
    }

    @Override
    protected Integer getAuthenticationLevelFromSAMLToken() {
        return Integer.parseInt(getSAMLAttributeValue(AUTHENTICATION_LEVEL));

    }

    @Override
    protected IdentType getIdentTypeFromSAMLToken() {
        return IdentType.valueOf(getSAMLAttributeValue(IDENT_TYPE));
    }

    @Override
    protected String getConsumerIdFromSAMLToken() {
        return getSAMLAttributeValue(CONSUMER_ID);
    }

    private String getSAMLAttributeValue(String samlAttributeName) {
        List<SAMLAttribute> samlAttributes = getSamlToken(getSubject()).getSAMLAttributes();
        String attributeValue;
        for (SAMLAttribute samlAttribute : samlAttributes) {
            attributeValue = samlAttribute.getStringAttributeValue()[0];
            if (samlAttributeName.equalsIgnoreCase(samlAttribute.getName())) {
                return attributeValue;
            }
        }
        return null;
    }

    private SAMLToken getSamlToken(Subject subject) {
        Set<SAMLToken> privcreds = subject.getPrivateCredentials(com.ibm.websphere.wssecurity.wssapi.token.SAMLToken.class);
        logger.debug("Private Credentials: " + privcreds);

        if (privcreds.size() != 1) {
            logger.error("Exactly one SAMLToken is allowed. This request contains " + privcreds.size());
            throw new ProtocolException("Exactly one SAMLToken is allowed. This request contains " + privcreds.size());
        }

        Iterator<SAMLToken> privcredsiterator = privcreds.iterator();
        SAMLToken samltoken = privcredsiterator.next();
        return samltoken;
    }
}
