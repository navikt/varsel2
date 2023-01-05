package no.nav.varsel.service.to;

import no.nav.varsel.domain.to.AktoerTo;
import no.nav.varsel.domain.to.MottakerType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BestillVarselToTest {

	private static final MottakerType MOTTAKER_TYPE = MottakerType.AKTOER;
	private static final String MOTTAKER = "mottaker";


	@Test
	public void shouldCreateAktoerTo() throws Exception {
		BestillVarselTo to = createTo();
		assertThat(to.isTestvarsel(), is(true));

		AktoerTo aktoerTo = to.createAktoerTo();

		assertThat(aktoerTo.getIdent(), is(MOTTAKER));
		assertThat(aktoerTo.getMottakerType(), is(MOTTAKER_TYPE));
	}

	static BestillVarselTo createTo() {
		BestillVarselTo to = new BestillVarselTo();
		to.setMottaker(AktoerTo.newAktoerId(MOTTAKER));
		to.setVarseltypeId("no/nav/varsel");
		to.setUtloepstidspunkt(LocalDateTime.now());
		to.getParameters().put("key", "val");
		to.setTestvarsel(true);
		return to;
	}
}