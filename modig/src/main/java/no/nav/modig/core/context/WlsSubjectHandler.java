package no.nav.modig.core.context;

import no.nav.modig.core.domain.IdentType;
import weblogic.security.Security;

import javax.security.auth.Subject;

public class WlsSubjectHandler extends SubjectHandler {

    @Override
    public Subject getSubject() {

        // get the Subject using WLS api
    	Subject subject = Security.getCurrentSubject();

    	return subject;
    }
    
    @Override
    protected IdentType getIdentTypeFromSAMLToken() {
        //TODO: Get from legacy SecurityContext until we can find a way to access the SAMLToken from the Subject, Oracle SR 3-7366034331
    	return getIdentTypeFromLegacySecurityHandler();
    }

    @Override
    protected Integer getAuthenticationLevelFromSAMLToken() {
    	//TODO: Get from legacy SecurityContext until we can find a way to access the SAMLToken from the Subject, Oracle SR 3-7366034331
    	return getAuthenticationLevelFromLegacySecurityContext();
    }

    @Override
    protected String getUidFromSAMLToken() {
    	//TODO: Get from legacy SecurityContext until we can find a way to access the SAMLToken from the Subject, Oracle SR 3-7366034331
        return getUidFromLegacySecurityContext();
    }

    @Override
    protected String getConsumerIdFromSAMLToken() {
    	//TODO: Get from legacy SecurityContext until we can find a way to access the SAMLToken from the Subject, Oracle SR 3-7366034331
        return getConsumerIdFromLegacySecurityHandler();
    }
    
    private String getUidFromLegacySecurityContext() {
        return getPrincipalFromSecurityContext() != null ? getPrincipalFromSecurityContext().getUserId() : null;
    }

    private Integer getAuthenticationLevelFromLegacySecurityContext() {
        return getPrincipalFromSecurityContext() != null ? Integer.valueOf(getPrincipalFromSecurityContext().getAuthenticationLevel()) : -1;
    }

    private IdentType getIdentTypeFromLegacySecurityHandler() {
        return getPrincipalFromSecurityContext() != null ? IdentType.valueOf(getPrincipalFromSecurityContext().getIdentType()) : null;
    }

    private String getConsumerIdFromLegacySecurityHandler() {
        return getPrincipalFromSecurityContext() != null ? getPrincipalFromSecurityContext().getConsumerId() : null;
    }

    private WlsPrincipal getPrincipalFromSecurityContext() {
        return WlsSecurityContext.getCurrent() != null ? WlsSecurityContext.getCurrent().getPrincipal() : null;
    }

}
