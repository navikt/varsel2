package no.nav.varsel.domain.utility;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateTimeConverter {

	public static LocalDateTime toLocalDateTime(DateTime jodaDateTime) {
		if (jodaDateTime == null) {
			return null;
		}

		Instant javaInstant = Instant.ofEpochMilli(jodaDateTime.getMillis());
		return LocalDateTime.ofInstant(javaInstant, ZoneId.of(jodaDateTime.getZone().getID()));
	}

	public static DateTime toJodaDateTime(LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return null;
		}

		String zoneId = "Europe/Oslo";
		ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of(zoneId));

		return new DateTime(zonedDateTime.toInstant().toEpochMilli(), DateTimeZone.forID(zoneId));
	}
}
