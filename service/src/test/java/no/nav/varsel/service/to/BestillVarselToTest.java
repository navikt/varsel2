package no.nav.varsel.service.to;

import static org.hamcrest.Matchers.is;

import no.nav.varsel.domain.to.AktoerTo;
import no.nav.varsel.domain.to.MottakerType;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.LocalDateTime;

/**
 * Unit test for {@link BestillVarselTo}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class BestillVarselToTest {

	private static final MottakerType MOTTAKER_TYPE = MottakerType.AKTOER;
	private static final String MOTTAKER = "mottaker";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void shouldCreateAktoerTo() throws Exception {
		BestillVarselTo to = createTo();
		Assert.assertThat(to.isTestvarsel(), is(true));

		AktoerTo aktoerTo = to.createAktoerTo();

		Assert.assertThat(aktoerTo.getIdent(), is(MOTTAKER));
		Assert.assertThat(aktoerTo.getMottakerType(), is(MOTTAKER_TYPE));
	}

	static BestillVarselTo createTo() {
		BestillVarselTo to = new BestillVarselTo();
		to.setMottaker(AktoerTo.newAktoerId(MOTTAKER));
		to.setVarseltypeId("varsel");
		to.setUtloepstidspunkt(LocalDateTime.now());
		to.getParameters().put("key", "val");
		to.setTestvarsel(true);
		return to;
	}
}