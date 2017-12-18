package no.nav.varsel.service.support;

import static no.nav.varsel.repo.TestdataUtil.AKTOR_ID;
import static no.nav.varsel.repo.TestdataUtil.FNR;
import static no.nav.varsel.repo.TestdataUtil.KANAL_CODE;
import static no.nav.varsel.repo.TestdataUtil.KONTAKT_INFO;
import static no.nav.varsel.repo.TestdataUtil.UTLOP_TIDSPUNKT;
import static no.nav.varsel.repo.TestdataUtil.VARSELTYPE_ID;
import static no.nav.varsel.repo.TestdataUtil.VARSEL_ID;
import static no.nav.varsel.repo.TestdataUtil.VARSEL_TEKST;
import static no.nav.varsel.repo.TestdataUtil.VARSEL_TITTEL;
import static no.nav.varsel.repo.TestdataUtil.VARSEL_URL;
import static no.nav.varsel.repo.TestdataUtil.createVarselbestilling;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.domain.to.MottakerType;
import no.nav.varsel.jms.producer.varselutsending.to.VarselutsendingTo;
import org.junit.Test;

import java.util.List;

/**
 * Unit test for {@link VarselutsendingToMapper}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselutsendingToMapperTest {
	
	private static String EPOSTADRESSE = "err@mock.com";
	private VarselutsendingToMapper mapper = new VarselutsendingToMapper();
	
	@Test
	public void shouldMapVarselbestilling() throws Exception {
		List<VarselutsendingTo> tos = mapper.map(createVarselbestilling());
		
		assertThat(tos, hasSize(1));
		assertVarselTo(tos.get(0));
		assertMottakerPerson(tos.get(0));
	}
	
	@Test
	public void shouldMapEpostVarsel() throws Exception {
		Varselbestilling varselbestilling = createVarselbestilling();
		
		List<VarselutsendingTo> tos = mapper.mapVarsels(varselbestilling, UTLOP_TIDSPUNKT,
				varselbestilling.getVarsels());
		
		assertThat(tos, hasSize(1));
		assertVarselTo(tos.get(0));
		assertMottakerPerson(tos.get(0));
	}
	
	@Test
	public void shouldRemoveWhitespaceFromKontaktinfo() throws Exception {
		Varselbestilling varselbestilling = createVarselbestilling();
		varselbestilling.getVarsels().iterator().next().setKanal(KanalCode.DITT_NAV);
		varselbestilling.getVarsels().forEach(e -> e.setKontaktInfo(" " + EPOSTADRESSE + " "));
		
		List<VarselutsendingTo> tos = mapper.mapVarsels(varselbestilling, UTLOP_TIDSPUNKT, varselbestilling.getVarsels());
		
		tos.forEach(e -> assertEquals(e.getKontaktInformasjon(), EPOSTADRESSE));
	}
	
	@Test
	public void shouldMapDittNavVarsel() throws Exception {
		Varselbestilling varselbestilling = createVarselbestilling();
		varselbestilling.getVarsels().iterator().next().setKanal(KanalCode.DITT_NAV);
		
		List<VarselutsendingTo> tos = mapper.mapVarsels(varselbestilling, UTLOP_TIDSPUNKT, varselbestilling.getVarsels());
		
		assertThat(tos, hasSize(1));
		assertVarselTo(tos.get(0));
		assertMottakerAktoer(tos.get(0));
	}
	
	private void assertVarselTo(VarselutsendingTo to) {
		assertThat(to.getVarselId(), is(VARSEL_ID));
		assertThat(to.getUtloepstidspunkt(), is(UTLOP_TIDSPUNKT));
		assertThat(to.getKontaktInformasjon(), is(KONTAKT_INFO));
		assertThat(to.getVarseltypeId(), is(VARSELTYPE_ID));
		assertThat(to.getVarselTittel(), is(VARSEL_TITTEL));
		assertThat(to.getVarselTekst(), is(VARSEL_TEKST));
		assertThat(to.getVarselUrl(), is(VARSEL_URL));
	}
	
	private void assertMottakerPerson(VarselutsendingTo to) {
		assertThat(to.getKanal(), is(KANAL_CODE));
		assertThat(to.getMottaker().getMottakerType(), is(MottakerType.PERSON));
		assertThat(to.getMottaker().getIdent(), is(FNR));
	}
	
	private void assertMottakerAktoer(VarselutsendingTo to) {
		assertThat(to.getKanal(), is(KanalCode.DITT_NAV));
		assertThat(to.getMottaker().getMottakerType(), is(MottakerType.AKTOER));
		assertThat(to.getMottaker().getIdent(), is(AKTOR_ID));
	}
}