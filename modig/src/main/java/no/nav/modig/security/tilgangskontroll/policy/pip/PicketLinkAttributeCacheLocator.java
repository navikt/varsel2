package no.nav.modig.security.tilgangskontroll.policy.pip;

import no.nav.modig.security.tilgangskontroll.policy.pip.cache.Cache;
import org.jboss.security.xacml.jaxb.Option;
import org.jboss.security.xacml.locators.AttributeLocator;
import org.jboss.security.xacml.locators.attrib.StorageAttributeLocator;
import org.jboss.security.xacml.sunxacml.EvaluationCtx;
import org.jboss.security.xacml.sunxacml.attr.AttributeDesignator;
import org.jboss.security.xacml.sunxacml.attr.AttributeValue;
import org.jboss.security.xacml.sunxacml.cond.EvaluationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.List;

import static org.jboss.security.xacml.util.JBossXACMLUtil.getTokenList;

public class PicketLinkAttributeCacheLocator extends StorageAttributeLocator {
    public static final Logger log = LoggerFactory
            .getLogger(PicketLinkAttributeCacheLocator.class);

    public static final String KEY_SUBJECT_ID = "cacheKeySubjectID";
    public static final String KEY_RESOURCE_ID = "cacheKeyResourceID";
    public static final String KEY_ACTION_ID = "cacheKeyActionID";
    public static final String KEY_ENVIRONMENT_ID = "cacheKeyEnvironmentID";
    public static final String OPTION_ATTRIBUTE_LOCATOR_CLASS = "attributeLocatorClass";

    private static final String PREFIX = "ATTRIBUTE";
    private static final String STRING_TYPE_IDENTIFIER = "http://www.w3.org/2001/XMLSchema#string";
    private static final URI STRING_TYPE_URI = URI.create(STRING_TYPE_IDENTIFIER);

    private LocatorDelegate delegate = new LocatorDelegate();
    private AttributeLocator locator;

    @Autowired
    private Cache<String, EvaluationResult> attributeCache;

    private static final String DEFAULT_PIP_CACHE_NAME = "pip.attribute";

    private static final String ATTRIBUTE_CACHE_NAME = "attributeCacheName";

    private String pipCacheName;

    @Override
    public EvaluationResult findAttribute(URI attributeType, URI attributeId,
            URI issuer, URI subjectCategory, EvaluationCtx context,
            int designatorType) {

            if (!ids.contains(attributeId)) {
                return delegate.createEmptyEvaluationResult(attributeType, attributeId);
            }

            // check in the cache
            EvaluationResult cacheInfo = get(attributeId, context);
            if (cacheInfo != null) {
                return cacheInfo;
            } else {
                locator = getLocatorInstance();
                EvaluationResult result = locator.findAttribute(attributeType,attributeId, issuer, subjectCategory, context, designatorType);
                put(attributeId, context, result);
                return result;
            }
    }


    private EvaluationResult get(URI attributeId, EvaluationCtx context) {
        String key;
        try {
            key = generateCacheKey(attributeId, context);
            return getCacheInstance().get(key, getPIPCacheName());
        } catch (Exception e) {
            log.error("Exception while retrieving attribute from cache. Most likely, there is a problem with the cache. Continuing without caching.", e);
        }
        return null;
    }

    private void put(URI attributeId, EvaluationCtx context,
                     EvaluationResult result) {
        String key;
        try {
            key = generateCacheKey(attributeId, context);
            getCacheInstance().put(key, result, getPIPCacheName());
        } catch (Exception e) {
            log.error("Exception while adding attribute to the cache. Most likely, there is a problem with the cache. Continuing without caching.", e);
        }

    }

    private Cache<String, EvaluationResult> getCacheInstance() {
        if (attributeCache != null) {
            return attributeCache;
        }
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        return attributeCache;
    }

    private AttributeLocator getLocatorInstance()  {
        if (locator != null) {
            return locator;
        }

        String locatorClassName = processOption(OPTION_ATTRIBUTE_LOCATOR_CLASS);
        try {
            locator = (AttributeLocator) loadClass(locatorClassName).newInstance();
        } catch (InstantiationException e) {
            throw  new RuntimeException("Problem instantiating class " + locatorClassName, e);
        } catch (IllegalAccessException e) {
            throw  new RuntimeException("Problem accessing class " + locatorClassName, e);
        } catch (ClassNotFoundException e) {
            throw  new RuntimeException("Could not find class " + locatorClassName, e);
        }
        locator.setOptions(options);
        return locator;
    }

    public String getPIPCacheName() {
        if (pipCacheName != null && !pipCacheName.isEmpty()) {
            return pipCacheName;
        }
        String cacheName = (String) processOption(ATTRIBUTE_CACHE_NAME);
        pipCacheName = (cacheName == null || cacheName.isEmpty()) ? DEFAULT_PIP_CACHE_NAME : cacheName;

        return pipCacheName;
    }

    @Override
    protected Object getSubstituteValue(URI attributeType, EvaluationCtx context)
            throws URISyntaxException {
        return null;
    }

    private Class<?> loadClass(String fqn) throws ClassNotFoundException {
        ClassLoader tcl = AccessController
                .doPrivileged(new PrivilegedAction<ClassLoader>() {
                    public ClassLoader run() {
                        return Thread.currentThread().getContextClassLoader();
                    }
                });
        return tcl.loadClass(fqn);
    }

    public String generateCacheKey(URI attributeId, EvaluationCtx context) {

        List<String> subjectID = getTokenList(processOption(KEY_SUBJECT_ID));
        List<String> resourceID = getTokenList(processOption(KEY_RESOURCE_ID));
        List<String> actionID = getTokenList(processOption(KEY_ACTION_ID));
        List<String> envID = getTokenList(processOption(KEY_ENVIRONMENT_ID));

        return generateCacheKey(attributeId, context, subjectID, resourceID, actionID, envID);
    }

    public static String generateCacheKey(URI attributeId, EvaluationCtx context, List<String> cacheKeySubjectIDs,
                                          List<String> cacheKeyResourceIDs, List<String> cacheKeyActionIDs, List<String> cacheKeyEnvIDs) {
        StringBuilder cacheKey = new StringBuilder();
        cacheKey.append(PREFIX).append("_").append(attributeId).append("_");

        if (cacheKeySubjectIDs != null) {
            cacheKey.append(processAttributes(context, cacheKeySubjectIDs,"subject"));
        }

        if (cacheKeyResourceIDs != null) {
            cacheKey.append(processAttributes(context, cacheKeyResourceIDs,"resource"));
        }

        if (cacheKeyActionIDs != null) {
            cacheKey.append(processAttributes(context, cacheKeyActionIDs,"action"));
        }

        if (cacheKeyEnvIDs != null) {
            cacheKey.append(processAttributes(context, cacheKeyEnvIDs,"environment"));
        }

        return cacheKey.toString();
    }

    private static StringBuilder processAttributes(EvaluationCtx context,
            List<String> cacheKeyIDs, String type) {

        StringBuilder partialCacheKey = new StringBuilder();

        Iterator<String> envIter = cacheKeyIDs != null ? cacheKeyIDs.iterator() : null;

        while (envIter != null && envIter.hasNext()) {
            if (type.equalsIgnoreCase("subject")) {
                partialCacheKey.append(getAttributeValueOrDefault(context.getSubjectAttribute(STRING_TYPE_URI, URI.create(envIter.next()), URI.create(AttributeDesignator.SUBJECT_CATEGORY_DEFAULT))));
            } else if (type.equalsIgnoreCase("resource")) {
                partialCacheKey.append(getAttributeValueOrDefault(context .getResourceAttribute(STRING_TYPE_URI, URI.create(envIter.next()), null)));
            } else if (type.equalsIgnoreCase("action")) {
                partialCacheKey.append(getAttributeValueOrDefault(context.getActionAttribute(STRING_TYPE_URI, URI.create(envIter.next()), null)));
            } else if (type.equalsIgnoreCase("environment")) {
                partialCacheKey.append(getAttributeValueOrDefault(context.getEnvironmentAttribute(STRING_TYPE_URI, URI.create(envIter.next()), null)));
            }
        }

        return partialCacheKey;
    }

    private static String getAttributeValueOrDefault(EvaluationResult result) {
        AttributeValue attributeValue = result.getAttributeValue();
        return (attributeValue == null) ? "" : (String) attributeValue
                .getValue();
    }

    private String processOption(String optionName) {
        for (Option option : options) {
            if (option.getName().equals(optionName)) {
                return (String) option.getContent().get(0);
            }
        }
        return "";
    }
}
