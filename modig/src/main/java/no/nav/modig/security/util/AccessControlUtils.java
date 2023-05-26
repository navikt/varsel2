package no.nav.modig.security.util;

import no.nav.modig.security.ws.AccessControlInterceptor;

public final class AccessControlUtils {
    private AccessControlUtils() {
    }

    public static boolean accessGranted() {
        return AccessControlInterceptor.accessGranted();
    }
}
