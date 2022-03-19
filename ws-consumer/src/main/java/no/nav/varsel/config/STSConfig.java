package no.nav.varsel.config;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * @author Ugur Alpay Cenar, Visma Consulting.
 */
@Component
@Profile("nais")
public class STSConfig {

    private final String stsUrl;
    private final String serviceuserUsername;
    private final String serviceuserPassword;

    @Inject
    public STSConfig(@Value("${securityTokenService.url}") String stsUrl,
					 @Value("${no.nav.modig.security.systemuser.username}") String serviceuserUsername,
                     @Value("${no.nav.modig.security.systemuser.password}") String serviceuserPassword) {
        this.stsUrl = stsUrl;
        this.serviceuserUsername = serviceuserUsername;
        this.serviceuserPassword = serviceuserPassword;
    }

    public void configureSTS(Object port) {
        Client client = ClientProxy.getClient(port);
        STSConfigUtil.configureStsRequestSamlToken(client, stsUrl, serviceuserUsername, serviceuserPassword);
    }
}
