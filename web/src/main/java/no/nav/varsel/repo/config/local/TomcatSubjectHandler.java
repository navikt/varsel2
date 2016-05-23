package no.nav.varsel.repo.config.local;

import no.nav.modig.core.context.SubjectHandler;
import org.apache.catalina.realm.GenericPrincipal;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.security.Principal;

/**
 * Subject handler for Mod to use when running locally with Tomcat
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class TomcatSubjectHandler extends SubjectHandler {
	private static String uid;

	@Override
	protected Subject getSubject() {
		ServletRequestAttributes servletRequestAttributes =
				(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (servletRequestAttributes == null) {
			throw new RuntimeException("Ingen request funnet på RequestContextHolder. \n" +
					"TomcatSubjectHandler krever at request holdes av Spring. \n" +
					"Benytt RequestContextListener eller RequestContextFilter til å ta vare på request. \n" +
					"Disse konfigureres opp i web.xml");
		}
		HttpServletRequest servletRequest = servletRequestAttributes.getRequest();

		if (servletRequest.getUserPrincipal() == null) {
			return null;
		}
		return lureOutSubjectFromPrincipal(servletRequest.getUserPrincipal());
	}

	/**
	 * Finding the subject in tomcat is a little tricky...
	 */
	private Subject lureOutSubjectFromPrincipal(Principal principal) {
		Field loginContextField = ReflectionUtils.findField(GenericPrincipal.class, "loginContext");
		loginContextField.setAccessible(true);
		try {
			LoginContext context = (LoginContext) loginContextField.get(principal);
			return context.getSubject();
		} catch (IllegalAccessException e) {
			return null;
		}
	}

	@Override
	protected String getUidFromSAMLToken() {
		return uid;
	}

	public static void setUid(String uid) {
		TomcatSubjectHandler.uid = uid;
	}
}
