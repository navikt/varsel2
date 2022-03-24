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
                         @Value("${varsel.serviceuser.username}") String serviceuserUsername,
                         @Value("${varsel.serviceuser.password}") String serviceuserPassword) {
        super(stsUrl, serviceuserUsername, serviceuserPassword);
    }

    @Override
    public void configureSTS(Object port) {
        // noop
    }
}
