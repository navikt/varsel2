package no.nav.varsel.service.tvarsel002.to;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for {@link MottaVarselKvitteringTo}
 *
 * @author Roar Bjurstrom, Visma Consulting.
 */
@ExtendWith(MockitoExtension.class)
public class MottaVarselKvitteringToTest {

	public static final String VARSEL_ID = UUID.randomUUID().toString();
	public static final String MOTTAKERINFORMASJON = "Mottakerinformasjon";
	public static final LocalDateTime UTSENDINGSTIDSPUNKT = LocalDateTime.of(2016, 1, 1, 1, 1);
	public static final MottaVarselKvitteringStatusTo STATUS_OK = MottaVarselKvitteringStatusTo.OK;
	public static final String FEILMELDING = "Feilmelding";


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
		Executable executable = () -> to.validateTo();
		Exception exception = Assertions.assertThrows(IllegalArgumentException.class, executable);
		assertEquals(exception.getMessage(), "varselId cannot be empty or missing");

	}

	@Test
	public void shouldValidateEmptyVarselId() throws Exception {
		MottaVarselKvitteringTo to = createTo();
		to.setVarselId("");
		Executable executable = () -> to.validateTo();
		Exception exception = Assertions.assertThrows(IllegalArgumentException.class, executable);
		assertEquals(exception.getMessage(), "varselId cannot be empty or missing");
	}

	@Test
	public void shouldValidateMissingUtsendingsTidspunkt() throws Exception {
		MottaVarselKvitteringTo to = createTo();
		to.setUtsendingstidspunkt(null);
		Executable executable = () -> to.validateTo();
		Exception exception = Assertions.assertThrows(IllegalArgumentException.class, executable);
		assertEquals(exception.getMessage(), "utsendingstidspunkt cannot be null");
	}

	@Test
	public void shouldValidateMissingStatus() throws Exception {
		MottaVarselKvitteringTo to = createTo();
		to.setStatus(null);
		Executable executable = () -> to.validateTo();
		Exception exception = Assertions.assertThrows(IllegalArgumentException.class, executable);
		assertEquals(exception.getMessage(), "status cannot be null");
	}

	public static MottaVarselKvitteringTo createTo() {
		MottaVarselKvitteringTo to = new MottaVarselKvitteringTo();
		to.setVarselId(VARSEL_ID);
		to.setMottakerInformasjon(MOTTAKERINFORMASJON);
		to.setUtsendingstidspunkt(UTSENDINGSTIDSPUNKT);
		to.setStatus(STATUS_OK);
		to.setFeilmelding(FEILMELDING);
		return to;
	}
}