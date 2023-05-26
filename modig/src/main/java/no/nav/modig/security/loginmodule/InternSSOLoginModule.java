package no.nav.modig.security.loginmodule;

import no.nav.modig.core.context.AuthenticationLevelCredential;
import no.nav.modig.core.domain.ConsumerId;
import no.nav.modig.core.domain.SluttBruker;
import org.jboss.security.SimplePrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.security.Principal;
import java.util.Map;
import java.util.Set;

/**
 * LoginModule that will use an enduser KerberosPrincipal and add NAV Principals and Credentials
 */
public class InternSSOLoginModule implements LoginModule {

    private static final Logger logger = LoggerFactory.getLogger(InternSSOLoginModule.class);
    private static final int AUTHENTICATION_LEVEL_INTERN_BRUKER = 4;
    private Subject subject;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
    }

    @Override
    public boolean commit() throws LoginException {
        logger.debug("enter commit");

        Set<KerberosPrincipal> kerberosprincipals = subject.getPrincipals(KerberosPrincipal.class);
        
        boolean kerberos = false;
        if(!kerberosprincipals.isEmpty()) {
        	// use kerberos if available
        	kerberos = true;
        }        

        String principal;
        
        if(kerberos) {
            logger.info("Using Kerberos");
        	if (kerberosprincipals.size() != 1) {
    			String feil = "Should only have one KerberosPrincipal, has " + kerberosprincipals.size();
                logger.error(feil);
                throw new LoginException(feil);
            }
        	
	        KerberosPrincipal kerberosPrincipal = (KerberosPrincipal) kerberosprincipals.toArray()[0];
	        logger.debug("In principal: " + kerberosPrincipal);
	
	        principal = kerberosPrincipal.getName();
	        int index = principal.indexOf('@');
	        if (index >= 0) {
	            principal = principal.substring(0, index);
	        } else {
	            throw new LoginException("KerberosPrincipal without expected domain");
	        }
        } else { // AD login
            logger.info("Using AD login");
        	Set<SimplePrincipal> adprincipals = subject.getPrincipals(SimplePrincipal.class);
        	// remove anything that is just a subclass of SimplePrincipal from the set (SimpleGroup which contains all AD-groups is a subclass of SimplePrincipal)
        	// using an array to avoid concurrentmodififactionexception (cannot remove from set while iterating over it)
        	Object[] adprincipalsarray = adprincipals.toArray();
        	if (adprincipalsarray.length > 1) {

                for (Object object: adprincipalsarray) {
        			SimplePrincipal sp = (SimplePrincipal) object;
        			if(sp.getClass() != SimplePrincipal.class) {
        				adprincipals.remove(sp);
        				logger.debug("Removing principal " + sp + " before looking for SimplePrincipal");
        			}        			
        		}
        	}
        	
        	if (adprincipals.size() != 1) {
    			String feil = "Should only have one SimplePrincipal, has " + adprincipals.size();
                logger.error(feil);
                throw new LoginException(feil);
            }
        	SimplePrincipal simplePrincipal = (SimplePrincipal) adprincipals.toArray()[0];
	        logger.debug("In principal: " + simplePrincipal);
	
	        principal = simplePrincipal.getName();
        }
        
        logger.debug("Storing principal: " + principal);

        subject.getPrincipals().add(SluttBruker.internBruker(principal));
        subject.getPublicCredentials().add(new AuthenticationLevelCredential(AUTHENTICATION_LEVEL_INTERN_BRUKER));

        subject.getPrincipals().add(new ConsumerId());

        logger.debug("return true");
        return true;
    }

    @Override
    public boolean login() throws LoginException {        
        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        return logout();
    }

    @Override
    public boolean logout() throws LoginException {
        logger.debug("enter logout");
        Set<Principal> principals = subject.getPrincipals();
        if (principals != null) {
            principals.removeAll(subject.getPrincipals(SluttBruker.class));
            principals.removeAll(subject.getPrincipals(ConsumerId.class));
        }


        Set<Object> publicCredentials = subject.getPublicCredentials();
        if (publicCredentials != null) {
            publicCredentials.removeAll(subject.getPublicCredentials(AuthenticationLevelCredential.class));
        }
        logger.debug("return true");
        return true;
    }

}
