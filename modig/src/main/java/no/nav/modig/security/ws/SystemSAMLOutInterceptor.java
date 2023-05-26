package no.nav.modig.security.ws;

import no.nav.modig.security.ws.attributes.SystemSAMLAttributes;

import java.util.Map;

import static org.apache.wss4j.common.ConfigurationConstants.SAML_CALLBACK_REF;

/**
 * CXF Soap interceptor som propagerer sikkerhetskontekst i form av et SAML token.
 * Propagerer systembrukers sikkerhetskontekst.
 *
 * Konfigureres med securityOutInterceptor.properties som holder følgende properties:
 *
 * org.apache.wss4j.crypto.merlin.keystore.file - default hentes fra system properties
 * org.apache.wss4j.crypto.merlin.keystore.password - default hentes fra system properties
 * org.apache.ws.security.saml.issuer.key.name (privatekeyentry)
 * org.apache.ws.security.saml.issuer.key.password - default hentes fra system properties
 * org.apache.ws.security.saml.issuer (applicationcert)
 * org.apache.ws.security.saml.issuer.sendKeyValue (false)
 *
 */
public class SystemSAMLOutInterceptor extends AbstractSAMLOutInterceptor {
    public SystemSAMLOutInterceptor() {
        super(false);
        getProperties().put(SAML_CALLBACK_REF, getCallbackHandler());
    }

    public SystemSAMLOutInterceptor(Map<String, Object> props) {
        super(false, props);
        getProperties().put(SAML_CALLBACK_REF, getCallbackHandler());
    }

    protected SAMLCallbackHandler getCallbackHandler() {
        return new SAMLCallbackHandler(new SystemSAMLAttributes());
    }
}
