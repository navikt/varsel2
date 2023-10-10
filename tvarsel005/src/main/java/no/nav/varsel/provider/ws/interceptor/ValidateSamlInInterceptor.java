package no.nav.varsel.provider.ws.interceptor;

import lombok.extern.slf4j.Slf4j;
import no.nav.modig.core.context.SubjectHandler;
import no.nav.modig.core.domain.IdentType;
import no.nav.modig.core.domain.SluttBruker;
import no.nav.varsel.config.ThreadLocalSubjectHandler;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.rt.security.saml.claims.SAMLSecurityContext;
import org.apache.cxf.security.SecurityContext;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.crypto.CryptoFactory;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.common.principal.SAMLTokenPrincipal;
import org.apache.wss4j.common.saml.SamlAssertionWrapper;
import org.apache.wss4j.dom.handler.RequestData;
import org.apache.wss4j.dom.handler.WSHandlerConstants;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.core.xml.schema.impl.XSAnyImpl;
import org.opensaml.saml.saml2.core.Attribute;

import javax.security.auth.Subject;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Slf4j
public class ValidateSamlInInterceptor extends WSS4JInInterceptor {

	public ValidateSamlInInterceptor(Map<String, Object> properties) {
		super(properties);
		setProperty(WSHandlerConstants.ACTION, WSHandlerConstants.SAML_TOKEN_SIGNED);
	}

	@Override
	public Crypto loadSignatureCrypto(RequestData requestData) throws WSSecurityException {

		Properties signatureProperties = new Properties();
		signatureProperties.setProperty("org.apache.wss4j.crypto.merlin.truststore.file", System.getProperty("jakarta.net.ssl.trustStore"));
		signatureProperties.setProperty("org.apache.wss4j.crypto.merlin.truststore.password", System.getProperty("jakarta.net.ssl.trustStorePassword"));

		return CryptoFactory.getInstance(signatureProperties);
	}

	@Override
	public void handleMessage(SoapMessage msg) {
		((ThreadLocalSubjectHandler) SubjectHandler.getSubjectHandler()).reset();
		super.handleMessage(msg);

		if (!isPingCall(msg)) {
			SAMLSecurityContext sc = (SAMLSecurityContext) msg.get(SecurityContext.class.getName());
			if (sc == null) {
				throw new RuntimeException("Cannot get SecurityContext from SoapMessage");
			}
			SAMLTokenPrincipal samlTokenPrincipal = (SAMLTokenPrincipal) sc.getUserPrincipal();
			if (samlTokenPrincipal == null) {
				throw new RuntimeException("Cannot get SAMLTokenPrincipal from SecurityContext");
			}

			((ThreadLocalSubjectHandler) SubjectHandler.getSubjectHandler()).setSubject(buildSubject(sc));
		}
	}


	private boolean isPingCall(SoapMessage msg) {
		return ((String) msg.getOrDefault("SOAPAction", "")).contains("/ping");
	}

	private Subject buildSubject(SAMLSecurityContext samlSecurityContext) {
		try {
			final SamlAssertionWrapper samlAssertionWrapper = new SamlAssertionWrapper(samlSecurityContext.getAssertionElement());
			Map<String, String> attributeValues = extractSamlAttributeValues(samlAssertionWrapper);
			final IdentType identType = IdentType.valueOf(attributeValues.getOrDefault("identType", IdentType.Prosess.name()));
			final Subject subject = new Subject();
			subject.getPrincipals().add(new SluttBruker(samlAssertionWrapper.getSubjectName(), identType));
			return subject;
		} catch (WSSecurityException e) {
			throw new RuntimeException("Unable to extract SAML assertion", e);
		}
	}

	private Map<String, String> extractSamlAttributeValues(SamlAssertionWrapper samlAssertionWrapper) {
		return samlAssertionWrapper.getSaml2().getAttributeStatements().stream()
				.flatMap(attributeStatement -> attributeStatement.getAttributes().stream())
				.collect(Collectors.toMap(Attribute::getName, attribute -> {
					final XMLObject xmlObject = attribute.getAttributeValues().get(0);
					if (xmlObject instanceof XSString) {
						return ((XSString) xmlObject).getValue();
					} else if (xmlObject instanceof XSAnyImpl) {
						return ((XSAnyImpl) xmlObject).getTextContent();
					} else {
						return "unknown";
					}
				}, (name1, name2) -> name1));
	}
}