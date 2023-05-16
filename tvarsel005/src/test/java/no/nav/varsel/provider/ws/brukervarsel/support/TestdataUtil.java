package no.nav.varsel.provider.ws.brukervarsel.support;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.GregorianCalendar;

import static java.util.Calendar.DATE;
import static java.util.Calendar.MILLISECOND;

public class TestdataUtil {
	public static DatatypeFactory DATATYPE_FACTORY;

	static {
		try {
			DATATYPE_FACTORY = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
	}

	public static XMLGregorianCalendar getXMLGregorianCalendar(int offsetFromNowInDays) {
		return DATATYPE_FACTORY.newXMLGregorianCalendar(getGregorianCalendar(offsetFromNowInDays));
	}

	public static XMLGregorianCalendar getXMLGregorianCalendar(int year, int month, int date) {
		return DATATYPE_FACTORY.newXMLGregorianCalendar(getGregorianCalendar(year, month, date));
	}

	private static GregorianCalendar getGregorianCalendar(int offsetFromNowInDays) {
		GregorianCalendar gregorianCalendar = (GregorianCalendar) GregorianCalendar.getInstance();
		gregorianCalendar.add(DATE, offsetFromNowInDays);
		return gregorianCalendar;
	}

	private static GregorianCalendar getGregorianCalendar(int year, int month, int date) {
		GregorianCalendar gregorianCalendar = (GregorianCalendar) GregorianCalendar.getInstance();
		gregorianCalendar.set(year, month, date, 0, 0, 0);
		gregorianCalendar.set(MILLISECOND, 0);
		return gregorianCalendar;
	}
}
