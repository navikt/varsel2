package no.nav.modig.security.tilgangskontroll.config;

import net.sf.ehcache.CacheManager;
import no.nav.modig.security.tilgangskontroll.policy.pip.cache.Cache;
import no.nav.modig.security.tilgangskontroll.policy.pip.cache.EhCache;
import org.jboss.security.xacml.sunxacml.cond.EvaluationResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.export.annotation.AnnotationJmxAttributeSource;
import org.springframework.jmx.export.assembler.MetadataMBeanInfoAssembler;
import org.springframework.jmx.export.naming.MetadataNamingStrategy;
import org.springframework.jmx.support.RegistrationPolicy;

@Configuration
public class SecurityCacheConfig {

    @Bean
    public Cache<String, EvaluationResult> cache() {
        EhCache<String, EvaluationResult> cache = new EhCache<>();
        cache.setCacheManager(cacheManager());
        return cache;
    }

    public CacheManager cacheManager() {
        return CacheManager.create();
    }

    @Bean
    public AnnotationJmxAttributeSource getAnnotationJmxAttributeSource() {
        return new AnnotationJmxAttributeSource();
    }

    @Bean
    public MetadataNamingStrategy getNamingStrategy() {
        MetadataNamingStrategy strategy = new MetadataNamingStrategy();
        strategy.setAttributeSource(getAnnotationJmxAttributeSource());
        return strategy;
    }

    @Bean
    public MetadataMBeanInfoAssembler getMbenInfoAssembler() {
        return new MetadataMBeanInfoAssembler(getAnnotationJmxAttributeSource());
    }

    @Bean
    public MBeanExporter getExporter() {
        MBeanExporter exporter = new MBeanExporter();
        exporter.setAutodetect(true);
        exporter.setNamingStrategy(getNamingStrategy());
        exporter.setAssembler(getMbenInfoAssembler());
        exporter.setRegistrationPolicy(RegistrationPolicy.REPLACE_EXISTING);
        return exporter;
    }
}
