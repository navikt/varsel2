package no.nav.modig.security.tilgangskontroll.policy.pip;

import org.jboss.security.xacml.interfaces.XACMLConstants;
import org.jboss.security.xacml.jaxb.Option;
import org.jboss.security.xacml.locators.attrib.LDAPAttributeLocator;
import org.jboss.security.xacml.sunxacml.EvaluationCtx;
import org.jboss.security.xacml.sunxacml.attr.AttributeDesignator;
import org.jboss.security.xacml.sunxacml.attr.AttributeValue;
import org.jboss.security.xacml.sunxacml.attr.BagAttribute;
import org.jboss.security.xacml.sunxacml.cond.EvaluationResult;
import org.jboss.security.xacml.util.JBossXACMLUtil;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.text.StrSubstitutor.replaceSystemProperties;

/**
 * Locator for retrieval of roles from AD via LDAP.
 * <p/>
 * Mandatory options for the locator are:
 * <p/>
 * 
 * <pre>
 * {@code
 * <ns:Option Name="url">url</ns:Option>
 * <ns:Option Name="username">userName</ns:Option>
 * <ns:Option Name="password">password</ns:Option>
 * <ns:Option Name="baseDN">baseDN</ns:Option>
 * }
 * </pre>
 * <p/>
 * With values inserted for each of the options, here is one example (username and password are omitted):
 * <p/>
 * 
 * <pre>
 * {@code
 * <ns:Option Name="url">ldap://ldapgw.test.local</ns:Option>
 * <ns:Option Name="baseDN">OU=BusinessUnits,DC=test,DC=local</ns:Option>
 * }
 * </pre>
 * <p/>
 * Note that variables are supported in all option values and are resolved using system properties at runtime:
 * 
 * <pre>
 * {@code
 * <ns:Option Name="url">${ldap.url}</ns:Option>
 * <ns:Option Name="username">${ldap.username}</ns:Option>
 * <ns:Option Name="password">${ldap.password}</ns:Option>
 * <ns:Option Name="baseDN">${ldap.basedn}</ns:Option>
 * }
 * </pre>
 * <p/>
 * <p/>
 * Default values will be supplied for the remaining options, listed in LDAPAttributeLocator
 */
public class LDAPRoleAttributeLocator extends LDAPAttributeLocator {
    private static final String STRING_TYPE_IDENTIFIER = "http://www.w3.org/2001/XMLSchema#string";
    private static final URI STRING_TYPE_URI = URI.create(STRING_TYPE_IDENTIFIER);

    private static final URI SUBJECT_ID_URI = URI.create(XACMLConstants.ATTRIBUTEID_SUBJECT_ID);

    /* default option values */
    private static final String ATTRIBUTE_OPTION = "attribute";
    private static final String ATTRIBUTE_VALUE_MEMBER_OF = "memberOf";
    private static final String FILTER_OPTION = "filter";
    private static final String FILTER_VALUE_COMMON_NAME = "(CN={0})";
    private static final String VALUE_DATA_TYPE_OPTION = "valueDataType";
    private static final String ATTRIBUTE_SUPPORTED_ID_OPTION = "attributeSupportedId";
    private static final String SUBSTITUTE_VALUE_OPTION = "substituteValue";

    private LocatorDelegate delegate = new LocatorDelegate();

    public LDAPRoleAttributeLocator() {
        super();
        ldapCommon = new LDAPCommon();
    }

    @Override
    public void setOptions(List<Option> theoptions) {
        addDefaultOptions(theoptions);
        super.setOptions(theoptions);
    }

    @Override
    public EvaluationResult findAttribute(URI attributeType, URI attributeId, URI issuer, URI subjectCategory, EvaluationCtx context, int designatorType) {
        if (!ids.contains(attributeId)) {
            return delegate.createEmptyEvaluationResult(attributeType, attributeId);
        }

        createJNDIContext();
        NamingEnumeration<SearchResult> results = searchLdap(getSubstituteValue(attributeType, context));
        closeJNDIContext();

        Set<AttributeValue> bagSet = traverseLdapResults(results);

        return new EvaluationResult(new BagAttribute(attributeType, bagSet));
    }

    @Override
    protected String getSubstituteValue(URI attributeType, EvaluationCtx context) {
        EvaluationResult evalResult = context.getSubjectAttribute(STRING_TYPE_URI, SUBJECT_ID_URI, URI.create(AttributeDesignator.SUBJECT_CATEGORY_DEFAULT));

        return (String) this.getAttributeValue(evalResult, attributeType);
    }

    private Set<AttributeValue> traverseLdapResults(NamingEnumeration<SearchResult> results) {
        Set<AttributeValue> roleBagSet = new HashSet<AttributeValue>();
        try {
            while (results.hasMore()) {
                SearchResult rs = results.next();
                retrieveRoleAttributes(roleBagSet, rs);
            }
        } catch (NamingException ignore) {
            try {
                results.close();
                throw new IllegalStateException(ignore);
            } catch (NamingException ignore2) {
                throw new IllegalStateException(ignore2);
            }
        }
        return roleBagSet;
    }

    private void addDefaultOptions(List<Option> theoptions) {
        theoptions.add(createOption(ATTRIBUTE_OPTION, ATTRIBUTE_VALUE_MEMBER_OF));
        theoptions.add(createOption(FILTER_OPTION, FILTER_VALUE_COMMON_NAME));
        theoptions.add(createOption(VALUE_DATA_TYPE_OPTION, STRING_TYPE_IDENTIFIER));
        theoptions.add(createOption(SUBSTITUTE_VALUE_OPTION, XACMLConstants.ATTRIBUTEID_SUBJECT_ID));
        theoptions.add(createOption(ATTRIBUTE_SUPPORTED_ID_OPTION, XACMLConstants.ATTRIBUTEID_ROLE));
    }

    private Option createOption(String optionName, String optionValue) {
        Option option = new Option();
        option.setName(optionName);
        option.getContent().add(optionValue);
        return option;
    }

    private void createJNDIContext() {
        try {
            ldapCommon.constructJNDIContext();
        } catch (NamingException e) {
            throw new IllegalStateException(e);
        }
    }

    private void closeJNDIContext() {
        try {
            ldapCommon.closeJNDIContext();
        } catch (NamingException ignore) {
            throw new IllegalStateException(ignore);
        }
    }

    private NamingEnumeration<SearchResult> searchLdap(String subjectId) {
        try {
            return ldapCommon.search(new Object[] { subjectId });
        } catch (NamingException e) {
            closeJNDIContext();
            throw new IllegalStateException(e);
        }
    }

    private void retrieveRoleAttributes(Set<AttributeValue> bagSet, SearchResult rs) throws NamingException {
        Attributes attributes = rs.getAttributes();
        if (attributes != null) {
            Attribute ldapAttribute = attributes.get(ldapCommon.getLdapAttribute());

            if (ldapAttribute != null) {
                addRoles(ldapAttribute.getAll(), bagSet);
            }
        }
    }

    private void addRoles(NamingEnumeration<?> allRoles, Set<AttributeValue> bagSet) {

        while (allRoles.hasMoreElements()) {
            String nextRole = parseLdapRoleEntry(allRoles.nextElement());
            bagSet.add(JBossXACMLUtil.getAttributeValue(nextRole));
        }
    }

    private String parseLdapRoleEntry(Object roleEntry) {
        if (!(roleEntry instanceof String)) {
            throw new IllegalStateException("Ldap role entry is not parsable as text");
        }
        String[] roleEntrySplitted = ((String) roleEntry).split(",");
        for (int i = 0; i < roleEntrySplitted.length; i++) {
            if (roleEntrySplitted[i].startsWith("CN=")) {
                return roleEntrySplitted[i].substring(3);
            }
        }
        return null;
    }

    /**
     * Subclass of {@link org.jboss.security.xacml.util.LDAPCommon} which interpolates option values using system properties
     * based on the ant expression syntax e.g. ${ldap.url}
     */
    public static class LDAPCommon extends org.jboss.security.xacml.util.LDAPCommon {

        @Override
        public void processPassedOption(String optionTag, String optionValue) {
            super.processPassedOption(optionTag, replaceSystemProperties(optionValue));
        }
    }
}