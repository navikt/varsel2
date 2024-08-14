package no.nav.varsel.consumer.dokmet.support;

import com.google.common.collect.Sets;
import no.nav.dokkat.schemas.tkat021.VarselInfoRestTo;
import no.nav.dokkat.schemas.tkat021.VarselMalRestTo;
import no.nav.varsel.consumer.dokmet.DokmetConsumerTest;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.consumer.dokmet.to.Varselinfo;
import no.nav.varsel.consumer.dokmet.to.Varselmal;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static no.nav.varsel.repo.TestdataUtil.ANTALL_REVARSLINGER;
import static no.nav.varsel.repo.TestdataUtil.VARSELTYPE_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class VarselinfoMapperTest {

	private final VarselinfoMapper mapper = new VarselinfoMapper();

	@Test
	public void shouldMap() {
		Varselinfo to = mapper.map(createVarselInfo());

		assertThat(to.getVarseltypeId(), is(VARSELTYPE_ID));
		assertThat(to.getVarselNavn(), Matchers.is(DokmetConsumerTest.VARSEL_NAVN));
		assertThat(to.getVarselForDistKanal(), Matchers.is(DokmetConsumerTest.VARSEL_FOR_DIST_KANAL));
		assertThat(to.getVarselKategori(), Matchers.is(DokmetConsumerTest.VARSEL_KATEGORI));
		assertThat(to.isInaktiv(), Matchers.is(DokmetConsumerTest.INAKTIV));
		assertThat(to.getRevarslingIntervall(), Matchers.is(DokmetConsumerTest.REVARSLING_INTERVALL));
		assertThat(to.getAntallRevarsling(), is(ANTALL_REVARSLINGER));
		assertThat(to.getVarselUrl(), Matchers.is(DokmetConsumerTest.VARSEL_URL));
		assertThat(to.getPreferertKanal(), contains(KanalCode.EPOST));
		assertThat(to.getMaler(), hasSize(1));

		Varselmal malTo = to.getMaler().iterator().next();
		assertThat(malTo.getKanal(), is(KanalCode.EPOST));
		assertThat(malTo.getTittel(), Matchers.is(DokmetConsumerTest.VARSEL_TITTEL));
		assertThat(malTo.getFoerstegangsTekst(), Matchers.is(DokmetConsumerTest.FOERSTE_GANG_TEKST));
		assertThat(malTo.getRevarslingTekst(), Matchers.is(DokmetConsumerTest.REVARSLING_TEKST));
	}

	@Test
	public void shouldHandleNullRevarslingIntervall() {
		VarselInfoRestTo varselInfo = createVarselInfo();
		varselInfo.setRevarslingIntervall(null);
		Varselinfo to = mapper.map(varselInfo);

		assertThat(to.getRevarslingIntervall(), nullValue());
	}

	@Test
	public void shouldHandleNullAntallRevarslinger() {
		VarselInfoRestTo varselInfo = createVarselInfo();
		varselInfo.setAntallRevarslinger(null);
		Varselinfo to = mapper.map(varselInfo);

		assertThat(to.getAntallRevarsling(), nullValue());
	}

	public static VarselInfoRestTo createVarselInfo() {
		VarselInfoRestTo varselInfo = new VarselInfoRestTo();
		varselInfo.setVarseltypeId(VARSELTYPE_ID);
		varselInfo.setVarselNavn(DokmetConsumerTest.VARSEL_NAVN);
		varselInfo.setVarselForDistribusjonKanal(DokmetConsumerTest.VARSEL_FOR_DIST_KANAL);
		varselInfo.setVarselKategori(DokmetConsumerTest.VARSEL_KATEGORI);
		varselInfo.setInaktiv(DokmetConsumerTest.INAKTIV);
		varselInfo.setRevarslingIntervall(DokmetConsumerTest.REVARSLING_INTERVALL);
		varselInfo.setAntallRevarslinger(ANTALL_REVARSLINGER);
		varselInfo.setVarselURL(DokmetConsumerTest.VARSEL_URL);
		varselInfo.setPreferertKanal(Sets.newHashSet(DokmetConsumerTest.PREFERERT_KANAL.toString()));

		VarselMalRestTo varselMal = new VarselMalRestTo();
		varselMal.setKanal(DokmetConsumerTest.PREFERERT_KANAL.toString());
		varselMal.setVarselTittel(DokmetConsumerTest.VARSEL_TITTEL);
		varselMal.setFoerstegangsvarselTekst(DokmetConsumerTest.FOERSTE_GANG_TEKST);
		varselMal.setRevarslingTekst(DokmetConsumerTest.REVARSLING_TEKST);

		varselInfo.setVarselmals(Sets.newHashSet(varselMal));
		return varselInfo;
	}
}