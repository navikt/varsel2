package no.nav.varsel.service.tvarsel005.support;

import static no.nav.varsel.repo.TestdataUtil.AKTOR_ID;
import static no.nav.varsel.repo.TestdataUtil.BESTILLING_TIDSPUNKT;
import static no.nav.varsel.repo.TestdataUtil.DISTRIBUSJON_TIDSPUNKT;
import static no.nav.varsel.repo.TestdataUtil.ER_REVARSEL;
import static no.nav.varsel.repo.TestdataUtil.FNR;
import static no.nav.varsel.repo.TestdataUtil.KANAL_CODE;
import static no.nav.varsel.repo.TestdataUtil.KONTAKT_INFO;
import static no.nav.varsel.repo.TestdataUtil.REVARSLING_INTERVALL;
import static no.nav.varsel.repo.TestdataUtil.SENDT_TIDSPUNKT;
import static no.nav.varsel.repo.TestdataUtil.VARSELTYPE_ID;
import static no.nav.varsel.repo.TestdataUtil.VARSEL_TEKST;
import static no.nav.varsel.repo.TestdataUtil.VARSEL_TITTEL;
import static no.nav.varsel.repo.TestdataUtil.VARSEL_URL;
import static no.nav.varsel.repo.TestdataUtil.createVarselBuilder;
import static no.nav.varsel.repo.TestdataUtil.createVarselbestillingBuilder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.Lists;
import no.nav.varsel.domain.code.StatusCode;
import no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerResponseTo;
import no.nav.varsel.service.tvarsel005.to.VarselTo;
import no.nav.varsel.service.tvarsel005.to.VarselbestillingTo;
import org.junit.Test;

import java.time.LocalDateTime;

/**
 * Unit test for {@link BrukervarselMapper}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class BrukervarselMapperTest {

	public static final LocalDateTime DISTRIBUSJON_TIDSPUNKT_NEWEST = DISTRIBUSJON_TIDSPUNKT.plusDays(1);
	private BrukervarselMapper mapper = new BrukervarselMapper();

	@Test
	public void shouldmap() throws Exception {
		HentVarselForBrukerResponseTo map = mapper.map(Lists.newArrayList(
				createVarselbestillingBuilder().varsels(
						// Remove opprettet
						createVarselBuilder().status(StatusCode.OPPRETTET).build(),
						createVarselBuilder().status(StatusCode.FERDIGBEHANDLET).build(),
						createVarselBuilder().distribusjonTidspunkt(DISTRIBUSJON_TIDSPUNKT_NEWEST).status(StatusCode.FERDIGBEHANDLET).build()
				).build(),
				// Remove varselbestilling with no varsels
				createVarselbestillingBuilder().varsels(
						// Remove sendt and feilet
						createVarselBuilder().status(StatusCode.SENDT).build(),
						createVarselBuilder().status(StatusCode.FEILET).build()
				).build()
		));

		assertThat(map.getVarselbestillingTos(), hasSize(1));
		VarselbestillingTo varselbestillingTo = map.getVarselbestillingTos().get(0);

		assertThat(varselbestillingTo.getVarseltypeId(), is(VARSELTYPE_ID));
		assertThat(varselbestillingTo.getFnr(), is(FNR));
		assertThat(varselbestillingTo.getAktoerId(), is(AKTOR_ID));
		assertThat(varselbestillingTo.getBestillingstidspunkt(), is(BESTILLING_TIDSPUNKT));
		assertThat(varselbestillingTo.getRevarslingsIntervall(), is(REVARSLING_INTERVALL));
		assertThat(varselbestillingTo.getSisteVarselUtsendelse(), is(DISTRIBUSJON_TIDSPUNKT_NEWEST));

		assertThat(varselbestillingTo.getVarsler().size(), is(2));
		VarselTo varselTo = varselbestillingTo.getVarsler().get(0);

		assertThat(varselTo.getKanal(), is(KANAL_CODE.toString()));
		assertThat(varselTo.getSendtTidspunkt(), is(SENDT_TIDSPUNKT));
		assertThat(varselTo.getKontaktInfo(), is(KONTAKT_INFO));
		assertThat(varselTo.getVarselTittel(), is(VARSEL_TITTEL));
		assertThat(varselTo.getVarselTekst(), is(VARSEL_TEKST));
		assertThat(varselTo.getVarselURL(), is(VARSEL_URL));
		assertThat(varselTo.isRevarsel(), is(ER_REVARSEL));

		LocalDateTime distTid1 = varselbestillingTo.getVarsler().get(0).getDistribusjonsTidspunkt();
		LocalDateTime distTid2 = varselbestillingTo.getVarsler().get(1).getDistribusjonsTidspunkt();
		assertTrue(
				(distTid1.equals(DISTRIBUSJON_TIDSPUNKT) && distTid2.equals(DISTRIBUSJON_TIDSPUNKT_NEWEST))
						|| (distTid2.equals(DISTRIBUSJON_TIDSPUNKT) && distTid1.equals(DISTRIBUSJON_TIDSPUNKT_NEWEST))
		);
	}

}