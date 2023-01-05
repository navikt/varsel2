package no.nav.varsel.domain.utility;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

	public static LocalDateTime toLocalDateTime(XMLGregorianCalendar xmlGregorianCalendar) {
		return xmlGregorianCalendar == null ? null :
				xmlGregorianCalendar.toGregorianCalendar().toZonedDateTime().toLocalDateTime();
	}
}
