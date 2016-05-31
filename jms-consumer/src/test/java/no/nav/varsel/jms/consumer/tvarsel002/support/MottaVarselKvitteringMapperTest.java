package no.nav.varsel.jms.consumer.tvarsel002.support;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import no.nav.melding.virksomhet.varselkvittering.v1.varselkvittering.VarselKvittering;
import no.nav.varsel.service.tvarsel002.to.MottaVarselKvitteringStatusTo;
import no.nav.varsel.service.tvarsel002.to.MottaVarselKvitteringTo;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Unit tests for {@link MottaVarselKvitteringMapper}
 *
 * @author Roar Bjurstrom, Visma Consulting.
 */
public class MottaVarselKvitteringMapperTest {

	private static final String UTSENDINGSSTIDSPUNKT = "2016-04-24T15:22:45";
	public  static final LocalDateTime DATE_UTSENDINGSSTIDSPUNKT = LocalDateTime.parse(UTSENDINGSSTIDSPUNKT);
	public static final String VARSEL_ID = UUID.randomUUID().toString();
	public static final String MOTTAKERINFORMASJON = "0000000000";
	public static final String STATUS = "plukket";
	public static final String FEILMELDING = "feilmelding";
	private static DatatypeFactory datatypeFactory;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private MottaVarselKvitteringMapper mapper = new MottaVarselKvitteringMapper();

	static {
		try {
			datatypeFactory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void shouldMap() throws Exception {
		MottaVarselKvitteringTo mapped = mapper.map(createVarselKvittering());

		assertThat(mapped.getVarselId(), equalTo(VARSEL_ID));
		assertThat(mapped.getMottakerInformasjon(), equalTo(MOTTAKERINFORMASJON));
		assertThat(mapped.getUtsendingstidspunkt(), equalTo(DATE_UTSENDINGSSTIDSPUNKT));
		assertThat(mapped.getStatus(), equalTo(MottaVarselKvitteringStatusTo.PLUKKET));
		assertThat(mapped.getFeilmelding(), equalTo(FEILMELDING));
	}

	@Test
	public void shouldMapWhenUtsendingstidspunktIsNull() throws Exception {
		VarselKvittering kvittering = createVarselKvittering();
		kvittering.setUtsendingstidspunkt(null);

		MottaVarselKvitteringTo mapped = mapper.map(kvittering);

		assertThat(mapped.getUtsendingstidspunkt(), nullValue());
	}

	@Test
	public void shouldMapWhenStatusIsNull() throws Exception {
		VarselKvittering kvittering = createVarselKvittering();
		kvittering.setStatus(null);

		MottaVarselKvitteringTo mapped = mapper.map(kvittering);

		assertThat(mapped.getStatus(), nullValue());
	}

	@Test
	public void shouldMapStatusFeilet() throws Exception {
		VarselKvittering kvittering = createVarselKvittering();
		kvittering.setStatus("feilet");

		MottaVarselKvitteringTo mapped = mapper.map(kvittering);

		assertThat(mapped.getStatus(), equalTo(MottaVarselKvitteringStatusTo.FEILET));
	}

	@Test
	public void shouldThrowIllegalArgumentIfUnkownStatus() throws Exception {
		expectedException.expect(IllegalArgumentException.class);

		VarselKvittering kvittering = createVarselKvittering();
		kvittering.setStatus("unknown");

		mapper.map(kvittering);
	}

	public static VarselKvittering createVarselKvittering() {
		VarselKvittering kvittering = new VarselKvittering();
		kvittering.setVarselId(VARSEL_ID);
		kvittering.setMottakerinformasjon(MOTTAKERINFORMASJON);
		kvittering.setUtsendingstidspunkt(datatypeFactory.newXMLGregorianCalendar(UTSENDINGSSTIDSPUNKT));
		kvittering.setStatus(STATUS);
		kvittering.setFeilmelding(FEILMELDING);
		return kvittering;
	}

}