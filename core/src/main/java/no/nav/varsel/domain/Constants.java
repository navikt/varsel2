package no.nav.varsel.domain;

import java.time.ZoneId;
import java.util.Locale;

public final class Constants {

	public static final String USER_ID = "userId";
	public static final Locale LOCALE_NO = Locale.forLanguageTag("no-nb");
	public static final ZoneId NORGE_ZONE = ZoneId.of("Europe/Oslo");

	private Constants() {
	}
}
