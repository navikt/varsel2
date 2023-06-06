package no.nav.varsel.domain.utility;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static no.nav.varsel.domain.utility.DateTimeConverter.toJodaDateTime;
import static no.nav.varsel.domain.utility.DateTimeConverter.toLocalDateTime;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class DateTimeConverterTest {

	@Test
	public void shouldConvertToLocalDateTime() {
		DateTime jodaDateTime = new DateTime("2023-05-24T10:11:12.123");

		LocalDateTime localDateTime = toLocalDateTime(jodaDateTime);

		LocalDateTime actual = LocalDateTime.parse("2023-05-24T10:11:12.123");

		assertThat(actual).isEqualTo(localDateTime);
	}

	@Test
	public void shouldConvertNullToLocalDateTime() {
		DateTime jodaDateTime = null;

		assertThat(toLocalDateTime(jodaDateTime)).isNull();
	}

	@Test
	public void shouldConvertToJodaDateTime() {
		LocalDateTime localDateTime = LocalDateTime.parse("2023-05-24T10:11:12.123");

		DateTime actual = toJodaDateTime(localDateTime);

		DateTime expected = new DateTime("2023-05-24T10:11:12.123", DateTimeZone.forID("Europe/Oslo"));

		assertThat(expected).isEqualTo(actual);
	}

	@Test
	public void shouldConvertNullToJodaDateTime() {
		LocalDateTime localDateTime = null;

		assertThat(toJodaDateTime(localDateTime)).isNull();
	}

}