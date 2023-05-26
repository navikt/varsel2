package no.nav.modig.security.tilgangskontroll.policy.pip.cache;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.collections15.Closure;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.PredicateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.collections15.CollectionUtils.forAllDo;
import static org.apache.commons.collections15.CollectionUtils.select;
import static org.apache.commons.collections15.PredicateUtils.equalPredicate;

@ManagedResource(objectName = "no.nav.modig.security.tilgangskontroll.policy.pip.cacheBean:name=ehCache", description = "Managed ehCache Bean")
public class EhCache<T, U> implements Cache<T, U> {

    private CacheManager cacheManager;

    private int cachesSize;

    private static final Logger LOG = LoggerFactory.getLogger(EhCache.class);

    @Override
    public void put(T key, U value, String cachename) {
        net.sf.ehcache.Ehcache cache = getCache(cachename);
        if (cache != null) {
            cache.put(new Element(key, value));
        }
    }

    private net.sf.ehcache.Ehcache getCache(String cachename) {
        net.sf.ehcache.Ehcache cache = cacheManager.getEhcache(cachename);
        return cache;
    }

    @Override
    public U get(T key, String cachename) {
        net.sf.ehcache.Ehcache cache = getCache(cachename);
        if (cache != null) {
            Element value = cache.get(key);

            if (value != null && !value.isExpired()) {
                return (U) value.getObjectValue();
            }
        }
        return null;
    }

    public void setCacheManager(CacheManager cacheManager2) {
        this.cacheManager = cacheManager2;
    }

    @ManagedAttribute(description = "Get the size value from all the caches")
    @Override
    public int getSize() {
        calculateAllCacheSize(PredicateUtils.<String> truePredicate());
        return cachesSize;
    }

    @ManagedAttribute(description = "Get the size value from the cache")
    public int getSize(String cacheName) {
        net.sf.ehcache.Ehcache cache = getCache(cacheName);
        if (cache != null) {
            return cache.getSize();
        }
        return 0;

    }

    private void calculateAllCacheSize(Predicate<String> cacheMatcher) {
        cachesSize = 0;
        Set<String> cacheName = new HashSet<String>(Arrays.asList(cacheManager
                .getCacheNames()));
        forAllDo(select(cacheName, cacheMatcher), getCacheSize());
    }

    private Closure<String> getCacheSize() {
        return new Closure<String>() {
            @Override
            public void execute(String cacheName) {
                if (cacheManager.getCache(cacheName) != null) {
                    cachesSize = cachesSize
                            + cacheManager.getCache(cacheName).getSize();
                }
            }
        };
    }

    @ManagedOperation(description = "Purge all the caches")
    @Override
    public void purge() {
        clear(PredicateUtils.<String> truePredicate());
    }

    @ManagedOperation(description = "Purge the cache")
    public void clear(String cacheName) {
//        clear(equalTo(cacheName));
        clear(equalPredicate(cacheName));
    }

    private void clear(Predicate<String> cacheMatcher) {
        Set<String> cacheName = new HashSet<String>(Arrays.asList(cacheManager
                .getCacheNames()));
        forAllDo(select(cacheName, cacheMatcher), clearCache());
    }

    private Closure<String> clearCache() {
        return new Closure<String>() {
            @Override
            public void execute(String cacheName) {
                cacheManager.getCache(cacheName).removeAll();
                LOG.info("Cleared cache '" + cacheName + "'");
            }
        };
    }

}