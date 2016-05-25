package no.nav.varsel.jms.consumer.tvarsel001.to;

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

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void shouldValidate() throws Exception {
		createTo().validate();
	}

	@Test
	public void shouldValidateMissingMottaker() throws Exception {
		expectedException.expectMessage("mottaker cannot be empty or missing");
		BestillServicemeldingTo to = createTo();
		to.setMottaker(null);
		to.validate();
	}

	@Test
	public void shouldValidateMissingVarslingstype() throws Exception {
		expectedException.expectMessage("varslingstype cannot be empty or missing");
		BestillServicemeldingTo to = createTo();
		to.setVarslingstype(null);
		to.validate();
	}

	@Test
	public void shouldValidateMissingParamKey() throws Exception {
		expectedException.expectMessage("parameter.key cannot be empty or missing");
		BestillServicemeldingTo to = createTo();
		to.getParameters().put(null, "val2");
		to.validate();
	}

	@Test
	public void shouldValidateMissingParamValue() throws Exception {
		expectedException.expectMessage("parameter.value cannot be empty or missing");
		BestillServicemeldingTo to = createTo();
		to.getParameters().put("key2", null);
		to.validate();
	}

	private BestillServicemeldingTo createTo() {
		BestillServicemeldingTo to = new BestillServicemeldingTo();
		to.setMottaker("mottaker");
		to.setVarslingstype("varsel");
		to.setUtloepstidspunkt(LocalDateTime.now());
		to.getParameters().put("key", "val");
		return to;
	}

}