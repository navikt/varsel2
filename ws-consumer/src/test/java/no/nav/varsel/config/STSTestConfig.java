package no.nav.varsel.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * @author Ugur Alpay Cenar, Visma Consulting.
 */
@Component
@Profile("itest")
public class STSTestConfig extends STSConfig {

    public STSTestConfig(@Value("${securitytokenservice.url}") String stsUrl,
                         @Value("${no.nav.modig.security.systemuser.username}") String serviceuserUsername,
                         @Value("${no.nav.modig.security.systemuser.password}") String serviceuserPassword) {
        super(stsUrl, serviceuserUsername, serviceuserPassword);
    }

    @Override
    public void configureSTS(Object port) {
        // noop
    }
}
