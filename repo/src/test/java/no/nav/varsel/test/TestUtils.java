package no.nav.varsel.test;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;

/**
 * Test utils
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class TestUtils {

	public static Matcher<? super LocalDateTime> aboutNow() {
		return new BaseMatcher<LocalDateTime>() {
			@Override
			public boolean matches(Object o) {
				if (o instanceof LocalDateTime) {
					int ms = Duration.between(LocalDateTime.now(), (Temporal) o).abs().getNano() / 1000000;
					return ms < 1000;
				}
				return false;
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("timestamp is not about now");
			}
		};
	}
}
