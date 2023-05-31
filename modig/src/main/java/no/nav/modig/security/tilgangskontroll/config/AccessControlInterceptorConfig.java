package no.nav.modig.security.tilgangskontroll.config;

import no.nav.modig.security.tilgangskontroll.policy.pep.AccessControlInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class AccessControlInterceptorConfig {

    @Bean
    public AccessControlInterceptor accessControlInterceptor() {
        return new AccessControlInterceptor();
    }
}
