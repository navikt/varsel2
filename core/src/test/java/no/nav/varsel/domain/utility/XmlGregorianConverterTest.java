package no.nav.varsel.domain.utility;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static no.nav.varsel.domain.utility.XmlGregorianConverter.DATATYPE_FACTORY;
import static no.nav.varsel.domain.utility.XmlGregorianConverter.toLocalDateTime;
import static no.nav.varsel.domain.utility.XmlGregorianConverter.toXmlGregorianCalendar;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;

public class XmlGregorianConverterTest {

	private static final String TIME_TEXT = "2016-05-04T21:21:42";
	private static final LocalDateTime TIME = LocalDateTime.parse(TIME_TEXT);

	@Test
	public void shouldConvertToXmlGregorianCalendar() throws Exception {
		XMLGregorianCalendar xmlGregorianCalendar = toXmlGregorianCalendar(TIME);
		assertThat(xmlGregorianCalendar.toString(), Matchers.is(TIME_TEXT + ".000" +
				ZoneId.systemDefault().getRules().getOffset(TIME).toString()));
	}

	@Test
	public void shouldConvertToXmlGregorianCalendarFromNull() throws Exception {
		assertThat(toXmlGregorianCalendar(null), nullValue());
	}

	@Test
	public void shouldConvertToLocalDateTime() throws Exception {
		LocalDateTime localDateTime = toLocalDateTime(DATATYPE_FACTORY.newXMLGregorianCalendar(TIME_TEXT));
		assertThat(localDateTime.toString(), Matchers.is(TIME_TEXT));
	}

	@Test
	public void shouldConvertToLocalDateTimeFromNull() throws Exception {
		assertThat(toLocalDateTime(null), nullValue());
	}

}