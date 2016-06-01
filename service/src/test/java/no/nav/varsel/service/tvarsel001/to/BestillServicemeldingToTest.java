package no.nav.varsel.service.tvarsel001.to;

import static org.hamcrest.Matchers.is;

import no.nav.varsel.domain.to.MottakerType;
import no.nav.varsel.domain.to.AktoerTo;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.LocalDateTime;

/**
 * Unit test for {@link BestillServicemeldingTo}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class BestillServicemeldingToTest {

	private static final MottakerType MOTTAKER_TYPE = MottakerType.AKTOER;
	private static final String MOTTAKER = "mottaker";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void shouldValidate() throws Exception {
		createTo().validateTvarsel001Input();
	}

	@Test
	public void shouldValidateMissingMottaker() throws Exception {
		expectedException.expectMessage("mottaker cannot be empty or missing");
		BestillServicemeldingTo to = createTo();
		to.setPersonIdent(null);
		to.setAktoerId(null);
		to.validateTvarsel001Input();
	}

	@Test
	public void shouldValidateMissingVarslingstype() throws Exception {
		expectedException.expectMessage("varslingstype cannot be empty or missing");
		BestillServicemeldingTo to = createTo();
		to.setVarslingstype(null);
		to.validateTvarsel001Input();
	}

	@Test
	public void shouldValidateMissingParamKey() throws Exception {
		expectedException.expectMessage("parameter.key cannot be empty or missing");
		BestillServicemeldingTo to = createTo();
		to.getParameters().put(null, "val2");
		to.validateTvarsel001Input();
	}

	@Test
	public void shouldValidateMissingParamValue() throws Exception {
		expectedException.expectMessage("parameter.value cannot be empty or missing");
		BestillServicemeldingTo to = createTo();
		to.getParameters().put("key2", null);
		to.validateTvarsel001Input();
	}

	@Test
	public void shouldCreateAktoerTo() throws Exception {
		AktoerTo aktoerTo = createTo().craeteAktoerTo();

		Assert.assertThat(aktoerTo.getIdent(), is(MOTTAKER));
		Assert.assertThat(aktoerTo.getMottakerType(), is(MOTTAKER_TYPE));
	}

	private BestillServicemeldingTo createTo() {
		BestillServicemeldingTo to = new BestillServicemeldingTo();
		to.setMottaker(new AktoerTo(MOTTAKER, MOTTAKER_TYPE));
		to.setVarslingstype("varsel");
		to.setUtloepstidspunkt(LocalDateTime.now());
		to.getParameters().put("key", "val");
		return to;
	}

}