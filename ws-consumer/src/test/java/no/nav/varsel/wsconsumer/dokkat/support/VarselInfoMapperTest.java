package no.nav.varsel.wsconsumer.dokkat.support;

import com.google.common.collect.Sets;
import no.nav.dokkat.schemas.tkat021.VarselInfoRestTo;
import no.nav.dokkat.schemas.tkat021.VarselMalRestTo;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo;
import no.nav.varsel.wsconsumer.dokkat.to.VarselMalTo;
import org.junit.jupiter.api.Test;

import static no.nav.varsel.repo.TestdataUtil.ANTALL_REVARSLINGER;
import static no.nav.varsel.repo.TestdataUtil.VARSELTYPE_ID;
import static no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumerTest.FOERSTE_GANG_TEKST;
import static no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumerTest.INAKTIV;
import static no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumerTest.PREFERERT_KANAL;
import static no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumerTest.REVARSLING_INTERVALL;
import static no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumerTest.REVARSLING_TEKST;
import static no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumerTest.VARSEL_FOR_DIST_KANAL;
import static no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumerTest.VARSEL_KATEGORI;
import static no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumerTest.VARSEL_NAVN;
import static no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumerTest.VARSEL_TITTEL;
import static no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumerTest.VARSEL_URL;
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
		assertThat(to.getVarselNavn(), is(VARSEL_NAVN));
		assertThat(to.getVarselForDistKanal(), is(VARSEL_FOR_DIST_KANAL));
		assertThat(to.getVarselKategori(), is(VARSEL_KATEGORI));
		assertThat(to.isInaktiv(), is(INAKTIV));
		assertThat(to.getRevarslingIntervall(), is(REVARSLING_INTERVALL));
		assertThat(to.getAntallRevarsling(), is(ANTALL_REVARSLINGER));
		assertThat(to.getVarselUrl(), is(VARSEL_URL));
		assertThat(to.getPreferertKanal(), contains(KanalCode.EPOST));
		assertThat(to.getMaler(), hasSize(1));

		VarselMalTo malTo = to.getMaler().iterator().next();
		assertThat(malTo.getKanal(), is(KanalCode.EPOST));
		assertThat(malTo.getTittel(), is(VARSEL_TITTEL));
		assertThat(malTo.getFoerstegangsTekst(), is(FOERSTE_GANG_TEKST));
		assertThat(malTo.getRevarslingTekst(), is(REVARSLING_TEKST));
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
		varselInfo.setVarselNavn(VARSEL_NAVN);
		varselInfo.setVarselForDistribusjonKanal(VARSEL_FOR_DIST_KANAL);
		varselInfo.setVarselKategori(VARSEL_KATEGORI);
		varselInfo.setInaktiv(INAKTIV);
		varselInfo.setRevarslingIntervall(REVARSLING_INTERVALL);
		varselInfo.setAntallRevarslinger(ANTALL_REVARSLINGER);
		varselInfo.setVarselURL(VARSEL_URL);
		varselInfo.setPreferertKanal(Sets.newHashSet(PREFERERT_KANAL.toString()));

		VarselMalRestTo varselMal = new VarselMalRestTo();
		varselMal.setKanal(PREFERERT_KANAL.toString());
		varselMal.setVarselTittel(VARSEL_TITTEL);
		varselMal.setFoerstegangsvarselTekst(FOERSTE_GANG_TEKST);
		varselMal.setRevarslingTekst(REVARSLING_TEKST);

		varselInfo.setVarselmals(Sets.newHashSet(varselMal));
		return varselInfo;
	}
}