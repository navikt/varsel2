package no.nav.varsel.wsconsumer.support;

import com.google.common.collect.Sets;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.wsconsumer.dkif.to.KontaktregisterTo;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashSet;

import static no.nav.varsel.wsconsumer.dkif.to.KontaktregisterTo.KontaktregisterToBuilder.aKontaktregisterTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

/**
 * Unit test for {@link VarselKanalDecider}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselKanalDeciderTest {

	private static final String EPOST = "epost@epost.no";
	private static final String MOBIL = "12345678";

	private static final HashSet<KanalCode> PREFERERT_SMS = Sets.newHashSet(KanalCode.SMS);
	private static final HashSet<KanalCode> PREFERERT_EPOST = Sets.newHashSet(KanalCode.EPOST);
	private static final HashSet<KanalCode> PREFERERT_SMS_EPOST = Sets.newHashSet(KanalCode.SMS, KanalCode.EPOST);
	private static final HashSet<KanalCode> PREFERERT_DITTNAV = Sets.newHashSet(KanalCode.DITT_NAV);
	private static final HashSet<KanalCode> PREFERERT_DITTNAV_OG_EPOST = Sets.newHashSet(KanalCode.DITT_NAV, KanalCode.EPOST);
	private static final HashSet<KanalCode> PREFERERT_DITTNAV_OG_SMS = Sets.newHashSet(KanalCode.DITT_NAV, KanalCode.SMS);

	private final VarselKanalDecider decider = new VarselKanalDecider();

	@Test
	public void shouldPrefDittNav() {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(EPOST, MOBIL), PREFERERT_DITTNAV);
		assertThat(kanaler, containsInAnyOrder(KanalCode.DITT_NAV));
	}

	@Test
	public void shouldPrefAll() {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(EPOST, MOBIL), Sets.newHashSet(KanalCode.SMS, KanalCode.EPOST, KanalCode.DITT_NAV));
		assertThat(kanaler, containsInAnyOrder(KanalCode.SMS, KanalCode.EPOST, KanalCode.DITT_NAV));
	}

	@Test
	public void shouldPrefSmsEpost() {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(EPOST, MOBIL), PREFERERT_SMS_EPOST);
		assertThat(kanaler, containsInAnyOrder(KanalCode.SMS, KanalCode.EPOST));
	}

	@Test
	public void shouldPrefSmsEpostOnlyEpost() {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(EPOST, null), PREFERERT_SMS_EPOST);
		assertThat(kanaler, containsInAnyOrder(KanalCode.EPOST));
	}

	@Test
	public void shouldPrefSmsEpostOnlySms() {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(null, MOBIL), PREFERERT_SMS_EPOST);
		assertThat(kanaler, containsInAnyOrder(KanalCode.SMS));
	}

	@Test
	public void shouldPrefSmsEpostFallbackDittNav() {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(null, null), PREFERERT_SMS_EPOST);
		assertThat(kanaler, containsInAnyOrder(KanalCode.DITT_NAV));
	}

	@Test
	public void shouldPrefSmsEpostOnNull() {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(EPOST, MOBIL), null);
		assertThat(kanaler, containsInAnyOrder(KanalCode.SMS, KanalCode.EPOST));
	}

	@Test
	public void shouldPrefEpostOnNullOnlyEpost() {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(EPOST, null), null);
		assertThat(kanaler, containsInAnyOrder(KanalCode.EPOST));
	}

	@Test
	public void shouldPrefSmsOnNullOnlySms() {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(null, MOBIL), null);
		assertThat(kanaler, containsInAnyOrder(KanalCode.SMS));
	}

	@Test
	public void shouldPrefSms() {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(EPOST, MOBIL), PREFERERT_SMS);
		assertThat(kanaler, containsInAnyOrder(KanalCode.SMS));
	}

	@Test
	public void shouldPrefSmsFallbackEpost() {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(EPOST, null), PREFERERT_SMS);
		assertThat(kanaler, containsInAnyOrder(KanalCode.EPOST));
	}

	@Test
	public void shouldPrefSmsFinalFallbackDittNav() {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(null, null), PREFERERT_SMS);
		assertThat(kanaler, containsInAnyOrder(KanalCode.DITT_NAV));
	}

	@Test
	public void shouldPrefEpost() {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(EPOST, MOBIL), PREFERERT_EPOST);
		assertThat(kanaler, containsInAnyOrder(KanalCode.EPOST));
	}

	@Test
	public void shouldPrefEpostFallbackSms() {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(null, MOBIL), PREFERERT_EPOST);
		assertThat(kanaler, containsInAnyOrder(KanalCode.SMS));
	}

	@Test
	public void shouldPrefEpostFinalFallbackDittNav() {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(null, null), PREFERERT_EPOST);
		assertThat(kanaler, containsInAnyOrder(KanalCode.DITT_NAV));
	}

	@Test
	public void decidesOnDittNavAndSmsWhenOnlyMobilInfoIsValidAndUsersPrefersEpostAndDittNav() {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(null, MOBIL), PREFERERT_DITTNAV_OG_EPOST);
		assertThat(kanaler, containsInAnyOrder(KanalCode.DITT_NAV, KanalCode.SMS));
	}

	@Test
	public void decidesOnDittNavAndEpostWhenOnlyEpostInfoIsValidAndUsersPrefersSmsAndDittNav() {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(EPOST, null), PREFERERT_DITTNAV_OG_SMS);
		assertThat(kanaler, containsInAnyOrder(KanalCode.DITT_NAV, KanalCode.EPOST));
	}

	private static KontaktregisterTo createKontaktTo(String epost, String mobil) {
		return aKontaktregisterTo()
				.epostadresse(epost)
				.mobiltelefonnummer(mobil)
				.build();
	}
}