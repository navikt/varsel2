package no.nav.modig.core.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ONLY FOR USE WITH WEBLOGIC
 */
public final class WlsSecurityContext {

    private static final ThreadLocal<WlsSecurityContext> HOLDER = new ThreadLocal<WlsSecurityContext>() {
        @Override
        protected WlsSecurityContext initialValue() {
            return new WlsSecurityContext();
        }
    };

    private final Map<String, Attribute> attributes = new ConcurrentHashMap<String, Attribute>();

    public static WlsSecurityContext getCurrent() {
        return HOLDER.get();
    }

    /**
     * Resets the current thread's local <code>Context</code> to a <b>new</b> default context object.
     * <p/>
     * <p/>
     * This method could be run when a request is finished to ensure that the thread is clean before it goes back to the thread
     * pool. Otherwise, data will be <b>WRONGLY</b> shared among different requests.
     */
    public static void reset() {
        HOLDER.set(new WlsSecurityContext());
    }

    public static void setCurrent(final WlsSecurityContext securityContext) {
        if(securityContext == null) {
        	throw new IllegalArgumentException("Context cannot be null");
        }
        HOLDER.set(securityContext);
    }

    public Attribute get(final Class<? extends Attribute> clazz) {
        if(clazz == null) {
        	throw new IllegalArgumentException("Class cannot be null");
        }
        return attributes.get(clazz.getName());
    }

    public void remove(final Class<? extends Attribute> clazz) {
    	if(clazz == null) {
        	throw new IllegalArgumentException("Class cannot be null");
        }
        attributes.remove(clazz.getName());
    }

    public void removeAll() {
        attributes.clear();
    }

    public void set(final Class<? extends Attribute> clazz,
            final Attribute attribute) {
        if(clazz == null) {
        	throw new IllegalArgumentException("Class cannot be null");
        }
        this.attributes.put(clazz.getName(), attribute);
    }

    public void setPrincipal(WlsPrincipal principal) {
        set(WlsPrincipal.class, principal);
    }

    public WlsPrincipal getPrincipal() {
        return (WlsPrincipal) get(WlsPrincipal.class);
    }

    public void setSamlAssertion(SAMLAssertion samlAssertion) {
        set(SAMLAssertion.class, samlAssertion);
    }

    public SAMLAssertion getSamlAssertion() {
        return (SAMLAssertion) get(SAMLAssertion.class);
    }

    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(getClass().getSimpleName());
        buffer.append(" [");

        boolean firstAcme = true;
        for (final String attribute : attributes.keySet()) {
            if (!firstAcme) {
                buffer.append(", ");
            }
            buffer.append(attribute).append('=')
                    .append(attributes.get(attribute));
            firstAcme = false;
        }

        buffer.append(']');

        return buffer.toString();
    }
}
