package no.nav.varsel.jms.consumer.tvarsel002.support;

import no.nav.melding.virksomhet.varselkvittering.v1.varselkvittering.VarselKvittering;
import no.nav.varsel.service.tvarsel002.to.MottaVarselKvitteringStatusTo;
import no.nav.varsel.service.tvarsel002.to.MottaVarselKvitteringTo;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static no.nav.varsel.domain.utility.XmlGregorianConverter.toXmlGregorianCalendar;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link MottaVarselKvitteringMapper}
 *
 * @author Roar Bjurstrom, Visma Consulting.
 */
public class MottaVarselKvitteringMapperTest {

	public static final LocalDateTime DATE_UTSENDINGSSTIDSPUNKT = LocalDateTime.parse("2016-04-24T15:22:45");
	public static final String VARSEL_ID = UUID.randomUUID().toString();
	public static final String MOTTAKERINFORMASJON = "0000000000";
	public static final String STATUS_OK = "ok";
	public static final String STATUS_ERROR = "Error";
	public static final String STATUS_EXPIRED = "EXPIRED";
	public static final String FEILMELDING = "feilmelding";

	private final MottaVarselKvitteringMapper mapper = new MottaVarselKvitteringMapper();

	@Test
	public void shouldMap() {
		MottaVarselKvitteringTo mapped = mapper.map(createVarselKvittering());

		assertThat(mapped.getVarselId(), equalTo(VARSEL_ID));
		assertThat(mapped.getMottakerInformasjon(), equalTo(MOTTAKERINFORMASJON));
		assertThat(mapped.getUtsendingstidspunkt(), equalTo(DATE_UTSENDINGSSTIDSPUNKT));
		assertThat(mapped.getStatus(), equalTo(MottaVarselKvitteringStatusTo.OK));
		assertThat(mapped.getFeilmelding(), equalTo(FEILMELDING));
	}

	@Test
	public void shouldMapWhenUtsendingstidspunktIsNull() {
		VarselKvittering kvittering = createVarselKvittering();
		kvittering.setUtsendingstidspunkt(null);

		MottaVarselKvitteringTo mapped = mapper.map(kvittering);

		assertThat(mapped.getUtsendingstidspunkt(), nullValue());
	}

	@Test
	public void shouldMapWhenStatusIsNull() {
		VarselKvittering kvittering = createVarselKvittering();
		kvittering.setStatus(null);

		MottaVarselKvitteringTo mapped = mapper.map(kvittering);

		assertThat(mapped.getStatus(), nullValue());
	}

	@Test
	public void shouldMapStatusError() {
		VarselKvittering kvittering = createVarselKvittering();
		kvittering.setStatus(STATUS_ERROR);

		MottaVarselKvitteringTo mapped = mapper.map(kvittering);

		assertThat(mapped.getStatus(), equalTo(MottaVarselKvitteringStatusTo.ERROR));
	}

	@Test
	public void shouldMapStatusExpired() {
		VarselKvittering kvittering = createVarselKvittering();
		kvittering.setStatus(STATUS_EXPIRED);

		MottaVarselKvitteringTo mapped = mapper.map(kvittering);

		assertThat(mapped.getStatus(), equalTo(MottaVarselKvitteringStatusTo.EXPIRED));
	}

	@Test
	public void shouldThrowIllegalArgumentIfUnkownStatus() {
		VarselKvittering kvittering = createVarselKvittering();
		kvittering.setStatus("unknown");

		assertThrows(IllegalArgumentException.class, () -> mapper.map(kvittering));
	}

	public static VarselKvittering createVarselKvittering() {
		VarselKvittering kvittering = new VarselKvittering();
		kvittering.setVarselId(VARSEL_ID);
		kvittering.setMottakerinformasjon(MOTTAKERINFORMASJON);
		kvittering.setUtsendingstidspunkt(toXmlGregorianCalendar(DATE_UTSENDINGSSTIDSPUNKT));
		kvittering.setStatus(STATUS_OK);
		kvittering.setFeilmelding(FEILMELDING);
		return kvittering;
	}

}