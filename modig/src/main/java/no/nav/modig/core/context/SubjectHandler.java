package no.nav.modig.core.context;

import no.nav.modig.core.domain.ConsumerId;
import no.nav.modig.core.domain.IdentType;
import no.nav.modig.core.domain.SluttBruker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import java.util.Set;

public abstract class SubjectHandler {

	public static final String SUBJECTHANDLER_KEY = "no.nav.modig.core.context.subjectHandlerImplementationClass";
	private static final Logger logger = LoggerFactory.getLogger(SubjectHandler.class);

	public static SubjectHandler getSubjectHandler() {

		String subjectHandlerImplementationClass = resolveProperty(SUBJECTHANDLER_KEY);

		if (subjectHandlerImplementationClass == null) {
			throw new RuntimeException("""
					Kunne ikke sette subjectHandlerImplementationClass. Om du kjører i jetty og test
					må du konfigurere opp en System property med key no.nav.modig.core.context.subjectHandlerImplementationClass.
					Dette kan gjøres på følgende måte:
					System.setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());
					""");
		}

		try {
			Class<?> clazz = Class.forName(subjectHandlerImplementationClass);
			return (SubjectHandler) clazz.newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Could not configure platform dependent subject handler", e);
		}
	}

	public abstract Subject getSubject();

	public String getUid() {
		if (!hasSubject()) {
			return null;
		}

		SluttBruker sluttBruker = getTheOnlyOneInSet(getSubject().getPrincipals(SluttBruker.class));
		if (sluttBruker != null) {
			return sluttBruker.getName();
		}

		return getUidFromSAMLToken();
	}

	public IdentType getIdentType() {
		if (!hasSubject()) {
			return null;
		}

		SluttBruker sluttBruker = getTheOnlyOneInSet(getSubject().getPrincipals(SluttBruker.class));
		if (sluttBruker != null) {
			return sluttBruker.getIdentType();
		}

		return getIdentTypeFromSAMLToken();
	}

	public String getConsumerId() {
		if (!hasSubject()) {
			return null;
		}

		ConsumerId consumerId = getTheOnlyOneInSet(getSubject().getPrincipals(ConsumerId.class));
		if (consumerId != null) {
			return consumerId.getConsumerId();
		}

		return getConsumerIdFromSAMLToken();
	}

	protected IdentType getIdentTypeFromSAMLToken() {
		return null;
	}

	protected String getUidFromSAMLToken() {
		return null;
	}

	protected String getConsumerIdFromSAMLToken() {
		return null;
	}

	private <T> T getTheOnlyOneInSet(Set<T> set) {
		if (set.isEmpty()) {
			return null;
		}

		T first = set.iterator().next();
		if (set.size() == 1) {
			return first;
		}

		logger.error("expected 1 (or zero) items, got " + set.size() + ", listing them:");
		for (T item : set) {
			logger.error(item.toString());
		}
		throw new IllegalStateException("To many (" + set.size() + ") " + first.getClass().getName() + ". Should be either 1 (logged in) og 0 (not logged in)");
	}

	private static String resolveProperty(String key) {
		String value = System.getProperty(key);
		if (value != null) {
			logger.debug("Setting " + key + "={} from System.properties", value);
		}
		return value;
	}

	private Boolean hasSubject() {
		return getSubject() != null;
	}
}
