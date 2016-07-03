package no.nav.varsel.provider.map.support;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Test data utility class
 *
 * @author Lars Aune.
 */
public class TestdataUtil {
	public static DatatypeFactory DATATYPE_FACTORY;

	static{
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
		XMLGregorianCalendar xmlGregorianCalendar = DATATYPE_FACTORY.newXMLGregorianCalendar(getGregorianCalendar(year, month, date));
		return xmlGregorianCalendar;
	}

	private static GregorianCalendar getGregorianCalendar(int offsetFromNowInDays) {
		GregorianCalendar gregorianCalendar = (GregorianCalendar) GregorianCalendar.getInstance();
		gregorianCalendar.add(Calendar.DATE, offsetFromNowInDays);
		return gregorianCalendar;
	}

	private static GregorianCalendar getGregorianCalendar(int year, int month, int date) {
		GregorianCalendar gregorianCalendar = (GregorianCalendar) GregorianCalendar.getInstance();
		gregorianCalendar.set(year, month, date, 0, 0, 0);
		gregorianCalendar.set(Calendar.MILLISECOND, 0);
		return gregorianCalendar;
	}
}
