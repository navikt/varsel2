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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class VarselinfoMapperTest {

	private static final String VARSEL_TITTEL = "Varsel Tittel";
	private static final String FOERSTE_GANG_TEKST = "Første gang tekst til {mottaker}";
	private static final boolean INAKTIV = false;
	private static final KanalCode PREFERERT_KANAL = EPOST;
	private static final String VARSEL_URL = "http://nav.no";

	@Test
	public void shouldMap() {
		Varselinfo to = mapToVarselinfo(createVarselInfoTo());

		assertThat(to.isInaktiv()).isEqualTo(INAKTIV);
		assertThat(to.getVarselUrl()).isEqualTo(VARSEL_URL);
		assertThat(to.getPreferertKanal()).contains(EPOST);

		assertThat(to.getMaler()).hasSize(1)
				.extracting("kanal", "tittel", "foerstegangsTekst")
				.containsExactlyElementsOf(
						singleton(tuple(EPOST, VARSEL_TITTEL, FOERSTE_GANG_TEKST))
				);
	}

	public static VarselInfoTo createVarselInfoTo() {
		var varselMalTo = VarselMalTo.builder()
				.kanal(PREFERERT_KANAL.toString())
				.varselTittel(VARSEL_TITTEL)
				.foerstegangsvarselTekst(FOERSTE_GANG_TEKST)
				.build();

		return VarselInfoTo.builder()
				.inaktiv(INAKTIV)
				.varselURL(VARSEL_URL)
				.preferertKanal(Set.of(PREFERERT_KANAL.toString()))
				.varselmals(Set.of(varselMalTo))
				.build();
	}
}