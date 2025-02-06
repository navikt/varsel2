package no.nav.varsel.tvarsel001.service.service.support;

import no.nav.varsel.tvarsel001.service.service.support.Varselutsending;
import no.nav.varsel.tvarsel001.service.service.support.VarselutsendingMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static no.nav.varsel.domain.code.KanalCode.DITT_NAV;
import static no.nav.varsel.domain.code.KanalCode.EPOST;
import static no.nav.varsel.domain.code.KanalCode.SMS;
import static no.nav.varsel.repo.TestdataUtil.VARSEL_TEKST;
import static no.nav.varsel.repo.TestdataUtil.VARSEL_TITTEL;
import static no.nav.varsel.repo.TestdataUtil.VARSEL_URL;
import static no.nav.varsel.repo.TestdataUtil.createVarselBuilder;
import static no.nav.varsel.repo.TestdataUtil.createVarselbestillingBuilder;
import static org.assertj.core.api.Assertions.assertThat;

public class VarselutsendingMapperTest {

	private final VarselutsendingMapper mapper = new VarselutsendingMapper();

	@Test
	public void shouldMapVarselbestilling() {
		var epostVarsel = createVarselBuilder()
				.kanal(EPOST)
				.build();
		var smsVarsel = createVarselBuilder()
				.kanal(SMS)
				.build();
		var dittNavVarsel = createVarselBuilder()
				.kanal(DITT_NAV)
				.build();
		var varselbestilling = createVarselbestillingBuilder()
									   .varsels(List.of(epostVarsel, smsVarsel, dittNavVarsel))
									   .build();

		List<Varselutsending> varselutsendingList = mapper.map(varselbestilling);

		assertThat(varselutsendingList)
				.hasSize(3)
				.allSatisfy(varsel -> {
					assertThat(varsel.getVarselUrl()).isEqualTo(VARSEL_URL);
					assertThat(varsel.getVarselTekst()).isEqualTo(VARSEL_TEKST);
					assertThat(varsel.getVarselTittel()).isEqualTo(VARSEL_TITTEL);
				})
				.extracting(Varselutsending::getKanal)
				.containsExactlyInAnyOrder(EPOST, SMS, DITT_NAV);
	}

}