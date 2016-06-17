package no.nav.varsel.wsconsumer.support;

import static no.nav.varsel.wsconsumer.dkif.to.KontaktregisterTo.KontaktregisterToBuilder.aKontaktregisterTo;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import com.google.common.collect.Sets;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.wsconsumer.dkif.to.KontaktregisterTo;
import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;

/**
 * Unit test for {@link VarslelKanalDecider}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarslelKanalDeciderTest {

	private static final String EPOST = "epost@epost.no";
	private static final String MOBIL = "12345678";

	public static final HashSet<KanalCode> PREFERERT_SMS = Sets.newHashSet(KanalCode.SMS);
	public static final HashSet<KanalCode> PREFERERT_EPOST = Sets.newHashSet(KanalCode.EPOST);
	public static final HashSet<KanalCode> PREFERERT_SMS_EPOST = Sets.newHashSet(KanalCode.SMS, KanalCode.EPOST);
	public static final HashSet<KanalCode> PREFERERT_DITTNAV = Sets.newHashSet(KanalCode.DITT_NAV);

	private VarslelKanalDecider decider = new VarslelKanalDecider();

	@Test
	public void shouldPrefDittNav() throws Exception {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(EPOST, MOBIL), PREFERERT_DITTNAV);
		assertThat(kanaler, containsInAnyOrder(KanalCode.DITT_NAV));
	}

	@Test
	public void shouldPrefSmsEpost() throws Exception {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(EPOST, MOBIL), PREFERERT_SMS_EPOST);
		assertThat(kanaler, containsInAnyOrder(KanalCode.SMS, KanalCode.EPOST));
	}

	@Test
	public void shouldPrefSmsEpostOnlyEpost() throws Exception {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(EPOST, null), PREFERERT_SMS_EPOST);
		assertThat(kanaler, containsInAnyOrder(KanalCode.EPOST));
	}

	@Test
	public void shouldPrefSmsEpostOnlySms() throws Exception {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(null, MOBIL), PREFERERT_SMS_EPOST);
		assertThat(kanaler, containsInAnyOrder(KanalCode.SMS));
	}

	@Test
	public void shouldPrefSmsEpostFallbackDittNav() throws Exception {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(null, null), PREFERERT_SMS_EPOST);
		assertThat(kanaler, containsInAnyOrder(KanalCode.DITT_NAV));
	}

	@Test
	public void shouldPrefSmsEpostOnNull() throws Exception {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(EPOST, MOBIL), null);
		assertThat(kanaler, containsInAnyOrder(KanalCode.SMS, KanalCode.EPOST));
	}


	@Test
	public void shouldPrefSms() throws Exception {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(EPOST, MOBIL), PREFERERT_SMS);
		assertThat(kanaler, containsInAnyOrder(KanalCode.SMS));
	}

	@Test
	public void shouldPrefSmsFallbackEpost() throws Exception {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(EPOST, null), PREFERERT_SMS);
		assertThat(kanaler, containsInAnyOrder(KanalCode.EPOST));
	}

	@Test
	public void shouldPrefSmsFinalFallbackDittNav() throws Exception {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(null, null), PREFERERT_SMS);
		assertThat(kanaler, containsInAnyOrder(KanalCode.DITT_NAV));
	}

	@Test
	public void shouldPrefEpost() throws Exception {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(EPOST, MOBIL), PREFERERT_EPOST);
		assertThat(kanaler, containsInAnyOrder(KanalCode.EPOST));
	}

	@Test
	public void shouldPrefEpostFallbackSms() throws Exception {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(null, MOBIL), PREFERERT_EPOST);
		assertThat(kanaler, containsInAnyOrder(KanalCode.SMS));
	}

	@Test
	public void shouldPrefEpostFinalFallbackDittNav() throws Exception {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(null, null), PREFERERT_EPOST);
		assertThat(kanaler, containsInAnyOrder(KanalCode.DITT_NAV));
	}

	private static KontaktregisterTo createKontaktTo(String epost, String mobil) {
		return aKontaktregisterTo()
				.epostadresse(epost)
				.mobiltelefonnummer(mobil)
				.build();
	}

}