package no.nav.varsel.service.support;

import no.nav.varsel.domain.object.Varselbestilling;
import org.junit.jupiter.api.Test;

import java.util.List;

import static no.nav.varsel.domain.code.KanalCode.DITT_NAV;
import static no.nav.varsel.repo.TestdataUtil.VARSEL_TEKST;
import static no.nav.varsel.repo.TestdataUtil.VARSEL_TITTEL;
import static no.nav.varsel.repo.TestdataUtil.VARSEL_URL;
import static no.nav.varsel.repo.TestdataUtil.createVarselbestilling;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class VarselutsendingMapperTest {

	private final VarselutsendingMapper mapper = new VarselutsendingMapper();

	@Test
	public void shouldMapVarselbestilling() {
		List<Varselutsending> tos = mapper.map(createVarselbestilling());

		assertThat(tos, hasSize(1));
		assertVarselTo(tos.get(0));
	}

	@Test
	public void shouldMapEpostVarsel() {
		Varselbestilling varselbestilling = createVarselbestilling();

		List<Varselutsending> tos = mapper.mapVarsler(varselbestilling.getVarsels());

		assertThat(tos, hasSize(1));
		assertVarselTo(tos.get(0));
	}

	@Test
	public void shouldMapDittNavVarsel() {
		Varselbestilling varselbestilling = createVarselbestilling();
		varselbestilling.getVarsels().iterator().next().setKanal(DITT_NAV);

		List<Varselutsending> tos = mapper.mapVarsler(varselbestilling.getVarsels());

		assertThat(tos, hasSize(1));
		assertVarselTo(tos.get(0));
	}

	private void assertVarselTo(Varselutsending to) {
		assertThat(to.getVarselTittel(), is(VARSEL_TITTEL));
		assertThat(to.getVarselTekst(), is(VARSEL_TEKST));
		assertThat(to.getVarselUrl(), is(VARSEL_URL));
	}
}