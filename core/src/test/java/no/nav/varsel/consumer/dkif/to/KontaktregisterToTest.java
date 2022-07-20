package no.nav.varsel.consumer.dkif.to;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static no.nav.varsel.consumer.dkif.to.KontaktregisterTo.DATE_VALID_MONTHS;
import static no.nav.varsel.consumer.dkif.to.KontaktregisterTo.KontaktregisterToBuilder.aKontaktregisterTo;
import static org.assertj.core.api.Assertions.assertThat;

class KontaktregisterToTest {

	public static final String EPOSTADRESSE = "bjarne@sesamstasjon.no";
	public static final String MOBILTELEFONNUMMER = "88888888";
	public static final LocalDateTime EPOSTADRESSE_OLD = LocalDateTime.now().minusMonths(DATE_VALID_MONTHS + 1);
	public static final LocalDateTime MOBILNUMMER_OLD = LocalDateTime.now().minusMonths(DATE_VALID_MONTHS + 1);

	@Test
	void shouldReturnFalseWhenMobilnummerDateValid() {
		KontaktregisterTo kontaktregisterTo = aKontaktregisterTo()
				.epostadresse(MOBILTELEFONNUMMER)
				.mobiltelefonSistOppdatert(MOBILNUMMER_OLD)
				.mobiltelefonSistVerifisert(LocalDateTime.now())
				.build();
		assertThat(kontaktregisterTo.isMobilDateInvalid()).isFalse();
	}

	@Test
	void shouldReturnTrueWhenMobilnummerDateInvalid() {
		KontaktregisterTo kontaktregisterTo = aKontaktregisterTo()
				.epostadresse(MOBILTELEFONNUMMER)
				.mobiltelefonSistOppdatert(MOBILNUMMER_OLD)
				.mobiltelefonSistVerifisert(MOBILNUMMER_OLD)
				.build();
		assertThat(kontaktregisterTo.isMobilDateInvalid()).isTrue();
	}

	@Test
	void shouldReturnFalseWhenEpostadresseDateValid() {
		KontaktregisterTo kontaktregisterTo = aKontaktregisterTo()
				.epostadresse(EPOSTADRESSE)
				.epostSistOppdatert(EPOSTADRESSE_OLD)
				.epostSistVerifisert(LocalDateTime.now())
				.build();
		assertThat(kontaktregisterTo.isEpostDateInvalid()).isFalse();
	}

	@Test
	void shouldReturnTrueWhenEpostadresseDateInvalid() {
		KontaktregisterTo kontaktregisterTo = aKontaktregisterTo()
				.epostadresse(EPOSTADRESSE)
				.epostSistOppdatert(EPOSTADRESSE_OLD)
				.epostSistVerifisert(EPOSTADRESSE_OLD)
				.build();
		assertThat(kontaktregisterTo.isEpostDateInvalid()).isTrue();
	}
}