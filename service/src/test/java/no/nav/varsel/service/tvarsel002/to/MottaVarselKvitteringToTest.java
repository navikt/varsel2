package no.nav.varsel.service.tvarsel002.to;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Unit test for {@link MottaVarselKvitteringTo}
 *
 * @author Roar Bjurstrom, Visma Consulting.
 */
@RunWith(MockitoJUnitRunner.class)
public class MottaVarselKvitteringToTest {

	public static final String VARSEL_ID = UUID.randomUUID().toString();
	public static final String MOTTAKERINFORMASJON = "Mottakerinformasjon";
	public static final LocalDateTime UTSENDINGSTIDSPUNKT = LocalDateTime.of(2016, 1, 1, 1, 1);
	public static final MottaVarselKvitteringStatusTo STATUS = MottaVarselKvitteringStatusTo.PLUKKET;
	public static final String FEILMELDING = "Feilmelding";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void shouldValidateTo() throws Exception {
		createTo().validateTo();
	}

	@Test
	public void shouldAllowNonMandatoryFieldToBeNull() throws Exception {
		MottaVarselKvitteringTo to = createTo();
		to.setMottakerInformasjon(null);
		to.setFeilmelding(null);

		to.validateTo();
	}

	@Test
	public void shouldValidateMissingVarselId() throws Exception {
		MottaVarselKvitteringTo to = createTo();
		to.setVarselId(null);

		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("varselId cannot be empty or missing");

		to.validateTo();
	}

	@Test
	public void shouldValidateEmptyVarselId() throws Exception {
		MottaVarselKvitteringTo to = createTo();
		to.setVarselId("");

		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("varselId cannot be empty or missing");

		to.validateTo();
	}

	@Test
	public void shouldValidateMissingUtsendingsTidspunkt() throws Exception {
		MottaVarselKvitteringTo to = createTo();
		to.setUtsendingstidspunkt(null);

		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("utsendingstidspunkt cannot be null");

		to.validateTo();
	}

	@Test
	public void shouldValidateMissingStatus() throws Exception {
		MottaVarselKvitteringTo to = createTo();
		to.setStatus(null);

		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("status cannot be null");

		to.validateTo();
	}

	public static MottaVarselKvitteringTo createTo() {
		MottaVarselKvitteringTo to = new MottaVarselKvitteringTo();
		to.setVarselId(VARSEL_ID);
		to.setMottakerInformasjon(MOTTAKERINFORMASJON);
		to.setUtsendingstidspunkt(UTSENDINGSTIDSPUNKT);
		to.setStatus(STATUS);
		to.setFeilmelding(FEILMELDING);
		return to;
	}
}