package no.nav.varsel.consumer.dokmet.support;

import no.nav.dokmet.api.tkat021.VarselInfoTo;
import no.nav.dokmet.api.tkat021.VarselMalTo;
import no.nav.varsel.consumer.dokmet.Varselinfo;
import no.nav.varsel.domain.code.KanalCode;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static java.util.Collections.singleton;
import static no.nav.varsel.consumer.dokmet.VarselinfoMapper.mapToVarselinfo;
import static no.nav.varsel.domain.code.KanalCode.EPOST;
import static no.nav.varsel.repo.TestdataUtil.ANTALL_REVARSLINGER;
import static no.nav.varsel.repo.TestdataUtil.VARSELTYPE_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class VarselinfoMapperTest {

	private static final String VARSEL_TITTEL = "Varsel Tittel";
	private static final String FOERSTE_GANG_TEKST = "Første gang tekst til {mottaker}";
	private static final String REVARSLING_TEKST = "Revarsling tekst til {mottaker}";
	private static final String VARSEL_FOR_DIST_KANAL = "vardistkanal";
	private static final String VARSEL_KATEGORI = "varkat";
	private static final boolean INAKTIV = false;
	private static final int REVARSLING_INTERVALL = 4;
	private static final KanalCode PREFERERT_KANAL = EPOST;
	private static final String VARSEL_NAVN = "varselnavn";
	private static final String VARSEL_URL = "http://nav.no";

	@Test
	public void shouldMap() {
		Varselinfo to = mapToVarselinfo(createVarselInfoTo());

		assertThat(to.getVarseltypeId()).isEqualTo(VARSELTYPE_ID);
		assertThat(to.getVarselNavn()).isEqualTo(VARSEL_NAVN);
		assertThat(to.getVarselForDistKanal()).isEqualTo(VARSEL_FOR_DIST_KANAL);
		assertThat(to.getVarselKategori()).isEqualTo(VARSEL_KATEGORI);
		assertThat(to.isInaktiv()).isEqualTo(INAKTIV);
		assertThat(to.getRevarslingIntervall()).isEqualTo(REVARSLING_INTERVALL);
		assertThat(to.getAntallRevarsling()).isEqualTo(ANTALL_REVARSLINGER);
		assertThat(to.getVarselUrl()).isEqualTo(VARSEL_URL);
		assertThat(to.getPreferertKanal()).contains(EPOST);

		assertThat(to.getMaler()).hasSize(1)
				.extracting("kanal", "tittel", "foerstegangsTekst", "revarslingTekst")
				.containsExactlyElementsOf(
						singleton(tuple(EPOST, VARSEL_TITTEL, FOERSTE_GANG_TEKST, REVARSLING_TEKST))
				);
	}

	@Test
	public void shouldHandleNullRevarslingIntervall() {
		var varselinfo = createVarselInfoTo();
		varselinfo.setRevarslingIntervall(null);

		Varselinfo to = mapToVarselinfo(varselinfo);

		assertThat(to.getRevarslingIntervall()).isNull();
	}

	@Test
	public void shouldHandleNullAntallRevarslinger() {
		var varselinfo = createVarselInfoTo();
		varselinfo.setAntallRevarslinger(null);

		Varselinfo to = mapToVarselinfo(varselinfo);

		assertThat(to.getAntallRevarsling()).isNull();
	}

	public static VarselInfoTo createVarselInfoTo() {
		var varselMalTo = VarselMalTo.builder()
				.kanal(PREFERERT_KANAL.toString())
				.varselTittel(VARSEL_TITTEL)
				.foerstegangsvarselTekst(FOERSTE_GANG_TEKST)
				.revarslingTekst(REVARSLING_TEKST)
				.build();

		return VarselInfoTo.builder()
				.varseltypeId(VARSELTYPE_ID)
				.varselNavn(VARSEL_NAVN)
				.varselForDistribusjonKanal(VARSEL_FOR_DIST_KANAL)
				.varselKategori(VARSEL_KATEGORI)
				.inaktiv(INAKTIV)
				.revarslingIntervall(REVARSLING_INTERVALL)
				.antallRevarslinger(ANTALL_REVARSLINGER)
				.varselURL(VARSEL_URL)
				.preferertKanal(Set.of(PREFERERT_KANAL.toString()))
				.varselmals(Set.of(varselMalTo))
				.build();
	}
}