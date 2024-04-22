package no.nav.modig.security.loginmodule;

import org.apache.wss4j.common.saml.SamlAssertionWrapper;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * A collection of utilities used to deserialize SAML token.
 */
class SamlUtils {

	private static final String IDENT_TYPE = "identType";
	private static final String AUTHENTICATION_LEVEL = "authenticationLevel";
	private static final String CONSUMER_ID = "consumerId";

	private static final Logger logger = LoggerFactory.getLogger(SamlUtils.class);

	public static SamlInfo getSamlInfo(Assertion samlToken) {
		String uid = samlToken.getSubject().getNameID().getValue();
		uid = filterDNtoCNvalue(uid);
		String identType = null;
		String authLevel = null;
		String consumerId = null;
		List<Attribute> attributes = samlToken.getAttributeStatements().get(0).getAttributes();
		for (Attribute attribute : attributes) {
			String attributeName = attribute.getName();
			String attributeValue = attribute.getAttributeValues().get(0)
					.getDOM().getFirstChild().getTextContent();

			if (IDENT_TYPE.equalsIgnoreCase(attributeName)) {
				identType = attributeValue;
			} else if (AUTHENTICATION_LEVEL.equalsIgnoreCase(attributeName)) {
				authLevel = attributeValue;
			} else if (CONSUMER_ID.equalsIgnoreCase(attributeName)) {
				consumerId = attributeValue;
			} else {
				logger.debug("Skipping SAML Attribute name: " + attribute.getName() + " value: " + attributeValue);
			}
		}
		if (uid == null) {
			throw new RuntimeException("SAML assertion is missing mandatory element NameId");
		}

		int iAuthLevel = -1;
		try {
			if (authLevel != null) {
				iAuthLevel = Integer.parseInt(authLevel);
			}
		} catch (NumberFormatException e) {
			throw new RuntimeException("AuthLevel attribute of SAML assertion is not a number", e);
		}

		return new SamlInfo(uid, identType, iAuthLevel, consumerId);
	}

	public static Assertion toSamlAssertion(String assertion) {
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setNamespaceAware(true);
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(new ByteArrayInputStream(assertion.getBytes(StandardCharsets.UTF_8)));

			SamlAssertionWrapper assertionWrapper = new SamlAssertionWrapper(document.getDocumentElement());
			return assertionWrapper.getSaml2();
		} catch (Exception e) {
			throw new RuntimeException("Could not deserialize SAML assertion", e);
		}

	}

	public static String filterDNtoCNvalue(String userid) {
		LdapName ldapname;
		try {
			ldapname = new LdapName(userid);
			String cn = null;
			for (Rdn rdn : ldapname.getRdns()) {
				if (rdn.getType().equalsIgnoreCase("CN")) {
					cn = rdn.getValue().toString();
					logger.debug("uid on DN form. Filtered from {} to {}", userid, cn);
					break;
				}
			}
			return cn;
		} catch (InvalidNameException e) {
			logger.debug("uid not on DN form. Skipping filter. {}", e.toString());
			return userid;
		}
	}


}
