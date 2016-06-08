package no.nav.varsel.service.support;

import static no.nav.varsel.repo.TestdataUtil.KANAL_CODE;
import static no.nav.varsel.repo.TestdataUtil.KONTAKT_INFO;
import static no.nav.varsel.repo.TestdataUtil.UTLOP_TIDSPUNKT;
import static no.nav.varsel.repo.TestdataUtil.VARSEL_ID;
import static no.nav.varsel.repo.TestdataUtil.VARSEL_TEKST;
import static no.nav.varsel.repo.TestdataUtil.VARSEL_TITTEL;
import static no.nav.varsel.repo.TestdataUtil.VARSEL_URL;
import static no.nav.varsel.repo.TestdataUtil.VARSLINGSTYPE;
import static no.nav.varsel.repo.TestdataUtil.createVarselbestilling;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import no.nav.varsel.domain.to.AktoerTo;
import no.nav.varsel.jms.producer.varselutsending.to.VarselutsendingTo;
import org.junit.Test;

import java.util.List;

/**
 * Unit test for {@link VarselutsendingToMapper}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselutsendingToMapperTest {

	private final AktoerTo aktoerTo = new AktoerTo();
	private VarselutsendingToMapper mapper = new VarselutsendingToMapper();

	@Test
	public void shouldMapVarselbestilling() throws Exception {
		AktoerTo aktoer = aktoerTo;
		List<VarselutsendingTo> tos = mapper.map(createVarselbestilling(), aktoer);

		assertThat(tos, hasSize(1));
		assertVarselTo(tos.get(0));
	}

	@Test
	public void shouldMapVarsel() throws Exception {
		List<VarselutsendingTo> tos = mapper.mapVarsels(aktoerTo, UTLOP_TIDSPUNKT, VARSLINGSTYPE,
				createVarselbestilling().getVarsels());

		assertThat(tos, hasSize(1));
		assertVarselTo(tos.get(0));
	}

	private void assertVarselTo(VarselutsendingTo to) {
		assertThat(to.getVarselId(), is(VARSEL_ID));
		assertThat(to.getMottaker(), is(aktoerTo));
		assertThat(to.getUtloepstidspunkt(), is(UTLOP_TIDSPUNKT));
		assertThat(to.getKanal(), is(KANAL_CODE));
		assertThat(to.getKontaktInformasjon(), is(KONTAKT_INFO));
		assertThat(to.getVarslingstype(), is(VARSLINGSTYPE));
		assertThat(to.getVarselTittel(), is(VARSEL_TITTEL));
		assertThat(to.getVarselTekst(), is(VARSEL_TEKST));
		assertThat(to.getVarselUrl(), is(VARSEL_URL));
	}
}