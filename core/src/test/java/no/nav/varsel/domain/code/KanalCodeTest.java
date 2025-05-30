package no.nav.varsel.domain.code;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static no.nav.varsel.domain.code.KanalCode.DITT_NAV;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;

public class KanalCodeTest {

	@ParameterizedTest
	@EnumSource(value = KanalCode.class, mode = EXCLUDE, names = "DITT_NAV")
	public void hasExternalUtsendingskanal(KanalCode kanalCode) {
		assertThat(kanalCode.hasExternalUtsendingskanal()).isTrue();
	}

	@Test
	public void hasInternalUtsendingskanal() {
		assertThat(DITT_NAV.hasExternalUtsendingskanal()).isFalse();
	}
}