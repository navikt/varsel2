package no.nav.varsel.wsconsumer.dokkat.support;

import static no.nav.varsel.repo.TestdataUtil.ANTALL_REVARSLINGER;
import static no.nav.varsel.repo.TestdataUtil.VARSLINGSTYPE;
import static no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumerTest.FOERSTE_GANG_TEKST;
import static no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumerTest.INAKTIV;
import static no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumerTest.PREFERERT_KANAL;
import static no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumerTest.REVARSLING_INTERVALL;
import static no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumerTest.REVARSLING_TEKST;
import static no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumerTest.VARSEL_FOR_DISTR_KANAL;
import static no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumerTest.VARSEL_KATEGORI;
import static no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumerTest.VARSEL_TITTEL;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import com.google.common.collect.Sets;
import no.nav.dokkat.schemas.tkat021.VarselInfo;
import no.nav.dokkat.schemas.tkat021.VarselMal;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo;
import no.nav.varsel.wsconsumer.dokkat.to.VarselMalTo;
import org.junit.Test;

/**
 * Unit test for {@link VarselInfoMapper}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselInfoMapperTest {

	private VarselInfoMapper mapper = new VarselInfoMapper();

	@Test
	public void shouldMap() throws Exception {
		VarselInfoTo to = mapper.map(createVarselInfo());

		assertThat(to.getVarslingstype(), is(VARSLINGSTYPE));
		assertThat(to.getVarselForDistrKanal(), is(VARSEL_FOR_DISTR_KANAL));
		assertThat(to.getVarselKategori(), is(VARSEL_KATEGORI));
		assertThat(to.isInaktiv(), is(INAKTIV));
		assertThat(to.getRevarslingIntervall(), is(REVARSLING_INTERVALL));
		assertThat(to.getAntallRevarsling(), is(ANTALL_REVARSLINGER));
		assertThat(to.getPreferertKanal(), contains(KanalCode.EPOST));
		assertThat(to.getMaler(), hasSize(1));

		VarselMalTo malTo = to.getMaler().iterator().next();
		assertThat(malTo.getKanal(), is(KanalCode.EPOST));
		assertThat(malTo.getTittel(), is(VARSEL_TITTEL));
		assertThat(malTo.getFoerstegangsTekst(), is(FOERSTE_GANG_TEKST));
		assertThat(malTo.getRevarslingTekst(), is(REVARSLING_TEKST));
	}

	public static VarselInfo createVarselInfo() {
		VarselInfo varselInfo = new VarselInfo();
		varselInfo.setVarslingstype(VARSLINGSTYPE);
		varselInfo.setVarselForDistribusjonKanal(VARSEL_FOR_DISTR_KANAL);
		varselInfo.setVarselKategori(VARSEL_KATEGORI);
		varselInfo.setInaktiv(INAKTIV);
		varselInfo.setRevarslingIntervall(REVARSLING_INTERVALL);
		varselInfo.setAntallRevarslinger(ANTALL_REVARSLINGER);
		varselInfo.setPreferertKanal(Sets.newHashSet(PREFERERT_KANAL.toString()));

		VarselMal varselMal = new VarselMal();
		varselMal.setKanal(PREFERERT_KANAL.toString());
		varselMal.setVarselTittel(VARSEL_TITTEL);
		varselMal.setFoerstegangsvarselTekst(FOERSTE_GANG_TEKST);
		varselMal.setRevarslingTekst(REVARSLING_TEKST);

		varselInfo.setVarselmals(Sets.newHashSet(varselMal));
		return varselInfo;
	}
}