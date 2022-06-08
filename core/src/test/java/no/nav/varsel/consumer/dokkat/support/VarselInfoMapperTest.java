package no.nav.varsel.consumer.dokkat.support;

import com.google.common.collect.Sets;
import no.nav.dokkat.schemas.tkat021.VarselInfoRestTo;
import no.nav.dokkat.schemas.tkat021.VarselMalRestTo;
import no.nav.varsel.consumer.dokkat.VarselInfoConsumerTest;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.consumer.dokkat.to.VarselInfoTo;
import no.nav.varsel.consumer.dokkat.to.VarselMalTo;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static no.nav.varsel.repo.TestdataUtil.ANTALL_REVARSLINGER;
import static no.nav.varsel.repo.TestdataUtil.VARSELTYPE_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class VarselInfoMapperTest {

	private final VarselInfoMapper mapper = new VarselInfoMapper();

	@Test
	public void shouldMap() {
		VarselInfoTo to = mapper.map(createVarselInfo());

		assertThat(to.getVarseltypeId(), is(VARSELTYPE_ID));
		assertThat(to.getVarselNavn(), Matchers.is(VarselInfoConsumerTest.VARSEL_NAVN));
		assertThat(to.getVarselForDistKanal(), Matchers.is(VarselInfoConsumerTest.VARSEL_FOR_DIST_KANAL));
		assertThat(to.getVarselKategori(), Matchers.is(VarselInfoConsumerTest.VARSEL_KATEGORI));
		assertThat(to.isInaktiv(), Matchers.is(VarselInfoConsumerTest.INAKTIV));
		assertThat(to.getRevarslingIntervall(), Matchers.is(VarselInfoConsumerTest.REVARSLING_INTERVALL));
		assertThat(to.getAntallRevarsling(), is(ANTALL_REVARSLINGER));
		assertThat(to.getVarselUrl(), Matchers.is(VarselInfoConsumerTest.VARSEL_URL));
		assertThat(to.getPreferertKanal(), contains(KanalCode.EPOST));
		assertThat(to.getMaler(), hasSize(1));

		VarselMalTo malTo = to.getMaler().iterator().next();
		assertThat(malTo.getKanal(), is(KanalCode.EPOST));
		assertThat(malTo.getTittel(), Matchers.is(VarselInfoConsumerTest.VARSEL_TITTEL));
		assertThat(malTo.getFoerstegangsTekst(), Matchers.is(VarselInfoConsumerTest.FOERSTE_GANG_TEKST));
		assertThat(malTo.getRevarslingTekst(), Matchers.is(VarselInfoConsumerTest.REVARSLING_TEKST));
	}

	@Test
	public void shouldHandleNullRevarslingIntervall() {
		VarselInfoRestTo varselInfo = createVarselInfo();
		varselInfo.setRevarslingIntervall(null);
		VarselInfoTo to = mapper.map(varselInfo);

		assertThat(to.getRevarslingIntervall(), nullValue());
	}

	@Test
	public void shouldHandleNullAntallRevarslinger() {
		VarselInfoRestTo varselInfo = createVarselInfo();
		varselInfo.setAntallRevarslinger(null);
		VarselInfoTo to = mapper.map(varselInfo);

		assertThat(to.getAntallRevarsling(), nullValue());
	}

	public static VarselInfoRestTo createVarselInfo() {
		VarselInfoRestTo varselInfo = new VarselInfoRestTo();
		varselInfo.setVarseltypeId(VARSELTYPE_ID);
		varselInfo.setVarselNavn(VarselInfoConsumerTest.VARSEL_NAVN);
		varselInfo.setVarselForDistribusjonKanal(VarselInfoConsumerTest.VARSEL_FOR_DIST_KANAL);
		varselInfo.setVarselKategori(VarselInfoConsumerTest.VARSEL_KATEGORI);
		varselInfo.setInaktiv(VarselInfoConsumerTest.INAKTIV);
		varselInfo.setRevarslingIntervall(VarselInfoConsumerTest.REVARSLING_INTERVALL);
		varselInfo.setAntallRevarslinger(ANTALL_REVARSLINGER);
		varselInfo.setVarselURL(VarselInfoConsumerTest.VARSEL_URL);
		varselInfo.setPreferertKanal(Sets.newHashSet(VarselInfoConsumerTest.PREFERERT_KANAL.toString()));

		VarselMalRestTo varselMal = new VarselMalRestTo();
		varselMal.setKanal(VarselInfoConsumerTest.PREFERERT_KANAL.toString());
		varselMal.setVarselTittel(VarselInfoConsumerTest.VARSEL_TITTEL);
		varselMal.setFoerstegangsvarselTekst(VarselInfoConsumerTest.FOERSTE_GANG_TEKST);
		varselMal.setRevarslingTekst(VarselInfoConsumerTest.REVARSLING_TEKST);

		varselInfo.setVarselmals(Sets.newHashSet(varselMal));
		return varselInfo;
	}
}