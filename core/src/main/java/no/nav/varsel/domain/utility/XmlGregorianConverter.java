package no.nav.varsel.domain.utility;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;

public class XmlGregorianConverter {

	static final DatatypeFactory DATATYPE_FACTORY;

	static {
		try {
			DATATYPE_FACTORY = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	public static XMLGregorianCalendar toXmlGregorianCalendar(LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return null;
		}
		GregorianCalendar calendar = GregorianCalendar.from(localDateTime.atZone(ZoneId.systemDefault()));
		return DATATYPE_FACTORY.newXMLGregorianCalendar(calendar);
	}

	public static LocalDateTime toLocalDateTimeFromGregorian(XMLGregorianCalendar xmlGregorianCalendar) {
		return xmlGregorianCalendar == null ? null :
				xmlGregorianCalendar.toGregorianCalendar().toZonedDateTime().toLocalDateTime();
	}

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
