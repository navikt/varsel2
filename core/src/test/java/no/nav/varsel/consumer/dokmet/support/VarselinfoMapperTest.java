package no.nav.varsel.consumer.dokmet.support;

import com.google.common.collect.Sets;
import no.nav.dokkat.schemas.tkat021.VarselInfoRestTo;
import no.nav.dokkat.schemas.tkat021.VarselMalRestTo;
import no.nav.varsel.consumer.dokmet.to.Varselinfo;
import no.nav.varsel.consumer.dokmet.to.Varselmal;
import no.nav.varsel.domain.code.KanalCode;
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

	private static final String VARSEL_TITTEL = "Varsel Tittel";
	private static final String FOERSTE_GANG_TEKST = "Første gang tekst til {mottaker}";
	private static final String REVARSLING_TEKST = "Revarsling tekst til {mottaker}";
	private static final String VARSEL_FOR_DIST_KANAL = "vardistkanal";
	private static final String VARSEL_KATEGORI = "varkat";
	private static final boolean INAKTIV = false;
	private static final int REVARSLING_INTERVALL = 4;
	private static final KanalCode PREFERERT_KANAL = KanalCode.EPOST;
	private static final String VARSEL_NAVN = "varselnavn";
	private static final String VARSEL_URL = "http://nav.no";

	private final VarselinfoMapper mapper = new VarselinfoMapper();

	@Test
	public void shouldMap() {
		Varselinfo to = mapper.map(createVarselInfo());

		assertThat(to.getVarseltypeId(), is(VARSELTYPE_ID));
		assertThat(to.getVarselNavn(), Matchers.is(VARSEL_NAVN));
		assertThat(to.getVarselForDistKanal(), Matchers.is(VARSEL_FOR_DIST_KANAL));
		assertThat(to.getVarselKategori(), Matchers.is(VARSEL_KATEGORI));
		assertThat(to.isInaktiv(), Matchers.is(INAKTIV));
		assertThat(to.getRevarslingIntervall(), Matchers.is(REVARSLING_INTERVALL));
		assertThat(to.getAntallRevarsling(), is(ANTALL_REVARSLINGER));
		assertThat(to.getVarselUrl(), Matchers.is(VARSEL_URL));
		assertThat(to.getPreferertKanal(), contains(KanalCode.EPOST));
		assertThat(to.getMaler(), hasSize(1));

		Varselmal malTo = to.getMaler().iterator().next();
		assertThat(malTo.getKanal(), is(KanalCode.EPOST));
		assertThat(malTo.getTittel(), Matchers.is(VARSEL_TITTEL));
		assertThat(malTo.getFoerstegangsTekst(), Matchers.is(FOERSTE_GANG_TEKST));
		assertThat(malTo.getRevarslingTekst(), Matchers.is(REVARSLING_TEKST));
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