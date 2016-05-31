package no.nav.varsel.domain.auxillary;

import static no.nav.varsel.domain.auxillary.XmlGregorianConverter.DATATYPE_FACTORY;
import static no.nav.varsel.domain.auxillary.XmlGregorianConverter.toLocalDateTime;
import static no.nav.varsel.domain.auxillary.XmlGregorianConverter.toXmlGregorianCalendar;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Unit test for {@link XmlGregorianConverter}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class XmlGregorianConverterTest {

	private static final String TIME_TEXT = "2016-05-04T21:21:42";
	private static final LocalDateTime TIME = LocalDateTime.parse(TIME_TEXT);

	@Test
	public void shouldConvertToXmlGregorianCalendar() throws Exception {
		XMLGregorianCalendar xmlGregorianCalendar = toXmlGregorianCalendar(TIME);
		assertThat(xmlGregorianCalendar.toString(), is(TIME_TEXT + ".000" +
				ZoneId.systemDefault().getRules().getOffset(TIME).toString()));
	}

	@Test
	public void shouldConvertToLocalDateTime() throws Exception {
		LocalDateTime localDateTime = toLocalDateTime(DATATYPE_FACTORY.newXMLGregorianCalendar(TIME_TEXT));
		assertThat(localDateTime.toString(), is(TIME_TEXT));
	}

}