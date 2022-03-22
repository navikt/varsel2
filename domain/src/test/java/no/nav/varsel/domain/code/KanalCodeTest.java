package no.nav.varsel.domain.code;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Unit test for {@link KanalCode}
 *
 * @author Roar Bjurstrom, Visma Consulting.
 */
public class KanalCodeTest {

	@Test
	public void hasExternalUtsendingskanal() throws Exception {
		assertThat(KanalCode.EPOST.hasExternalUtsendingskanal(), is(true));
		assertThat(KanalCode.SMS.hasExternalUtsendingskanal(), is(true));
	}

	@Test
	public void hasInternalUtsendingskanal() throws Exception {
		assertThat(KanalCode.DITT_NAV.hasExternalUtsendingskanal(), is(false));
	}
}