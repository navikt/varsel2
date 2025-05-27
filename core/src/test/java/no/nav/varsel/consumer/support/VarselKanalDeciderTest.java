package no.nav.varsel.consumer.support;

import com.google.common.collect.Sets;
import no.nav.varsel.consumer.dkif.to.KontaktregisterTo;
import no.nav.varsel.domain.code.KanalCode;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashSet;

import static no.nav.varsel.domain.code.KanalCode.DITT_NAV;
import static no.nav.varsel.domain.code.KanalCode.EPOST;
import static no.nav.varsel.domain.code.KanalCode.SMS;
import static org.assertj.core.api.Assertions.assertThat;

public class VarselKanalDeciderTest {

	private static final String KONTAKTINFO_EPOST = "epost@epost.no";
	private static final String KONTAKTINFO_MOBIL = "12345678";

	private static final HashSet<KanalCode> PREFERERT_KANAL_KUN_SMS = Sets.newHashSet(SMS);
	private static final HashSet<KanalCode> PREFERERT_KANAL_KUN_EPOST = Sets.newHashSet(EPOST);
	private static final HashSet<KanalCode> PREFERERT_KANAL_KUN_DITTNAV = Sets.newHashSet(DITT_NAV);
	private static final HashSet<KanalCode> PREFERERT_KANAL_BAADE_SMS_OG_EPOST = Sets.newHashSet(SMS, EPOST);
	private static final HashSet<KanalCode> PREFERERT_KANAL_BAADE_DITTNAV_OG_EPOST = Sets.newHashSet(DITT_NAV, EPOST);
	private static final HashSet<KanalCode> PREFERERT_KANAL_BAADE_DITTNAV_OG_SMS = Sets.newHashSet(DITT_NAV, SMS);
	private static final HashSet<KanalCode> PREFERERT_KANAL_ALLE = Sets.newHashSet(SMS, EPOST, DITT_NAV);

	private final VarselKanalDecider decider = new VarselKanalDecider();

	@Test
	public void shouldPrefDittNav() {
		Collection<KanalCode> kanaler = decider.decideKanaler(kontaktMedBaadeEpostOgMobil(), PREFERERT_KANAL_KUN_DITTNAV);

		assertThat(kanaler).containsExactlyInAnyOrder(DITT_NAV);
	}

	@Test
	public void shouldPrefAll() {
		Collection<KanalCode> kanaler = decider.decideKanaler(kontaktMedBaadeEpostOgMobil(), PREFERERT_KANAL_ALLE);

		assertThat(kanaler).containsExactlyInAnyOrder(SMS, EPOST, DITT_NAV);
	}

	@Test
	public void shouldPrefSmsEpost() {
		Collection<KanalCode> kanaler = decider.decideKanaler(kontaktMedBaadeEpostOgMobil(), PREFERERT_KANAL_BAADE_SMS_OG_EPOST);

		assertThat(kanaler).containsExactlyInAnyOrder(SMS, EPOST);
	}

	@Test
	public void shouldPrefSmsEpostOnlyEpost() {
		Collection<KanalCode> kanaler = decider.decideKanaler(kontaktMedKunEpost(), PREFERERT_KANAL_BAADE_SMS_OG_EPOST);

		assertThat(kanaler).containsExactlyInAnyOrder(EPOST);
	}

	@Test
	public void shouldPrefSmsEpostOnlySms() {
		Collection<KanalCode> kanaler = decider.decideKanaler(kontaktMedKunMobil(), PREFERERT_KANAL_BAADE_SMS_OG_EPOST);

		assertThat(kanaler).containsExactlyInAnyOrder(SMS);
	}

	@Test
	public void shouldPrefSmsEpostFallbackDittNav() {
		Collection<KanalCode> kanaler = decider.decideKanaler(kontaktUtenEpostOgMobil(), PREFERERT_KANAL_BAADE_SMS_OG_EPOST);

		assertThat(kanaler).containsExactlyInAnyOrder(DITT_NAV);
	}

	@Test
	public void shouldPrefSmsEpostOnNull() {
		Collection<KanalCode> kanaler = decider.decideKanaler(kontaktMedBaadeEpostOgMobil(), null);

		assertThat(kanaler).containsExactlyInAnyOrder(SMS, EPOST);
	}

	@Test
	public void shouldPrefEpostOnNullOnlyEpost() {
		Collection<KanalCode> kanaler = decider.decideKanaler(kontaktMedKunEpost(), null);

		assertThat(kanaler).containsExactlyInAnyOrder(EPOST);
	}

	@Test
	public void shouldPrefSmsOnNullOnlySms() {
		Collection<KanalCode> kanaler = decider.decideKanaler(kontaktMedKunMobil(), null);

		assertThat(kanaler).containsExactlyInAnyOrder(SMS);
	}

	@Test
	public void shouldPrefSms() {
		Collection<KanalCode> kanaler = decider.decideKanaler(kontaktMedBaadeEpostOgMobil(), PREFERERT_KANAL_KUN_SMS);

		assertThat(kanaler).containsExactlyInAnyOrder(SMS);
	}

	@Test
	public void shouldPrefSmsFallbackEpost() {
		Collection<KanalCode> kanaler = decider.decideKanaler(kontaktMedKunEpost(), PREFERERT_KANAL_KUN_SMS);

		assertThat(kanaler).containsExactlyInAnyOrder(EPOST);
	}

	@Test
	public void shouldPrefSmsFinalFallbackDittNav() {
		Collection<KanalCode> kanaler = decider.decideKanaler(kontaktUtenEpostOgMobil(), PREFERERT_KANAL_KUN_SMS);

		assertThat(kanaler).containsExactlyInAnyOrder(DITT_NAV);
	}

	@Test
	public void shouldPrefEpost() {
		Collection<KanalCode> kanaler = decider.decideKanaler(kontaktMedBaadeEpostOgMobil(), PREFERERT_KANAL_KUN_EPOST);

		assertThat(kanaler).containsExactlyInAnyOrder(EPOST);
	}

	@Test
	public void shouldPrefEpostFallbackSms() {
		Collection<KanalCode> kanaler = decider.decideKanaler(kontaktMedKunMobil(), PREFERERT_KANAL_KUN_EPOST);

		assertThat(kanaler).containsExactlyInAnyOrder(SMS);
	}

	@Test
	public void shouldPrefEpostFinalFallbackDittNav() {
		Collection<KanalCode> kanaler = decider.decideKanaler(kontaktUtenEpostOgMobil(), PREFERERT_KANAL_KUN_EPOST);

		assertThat(kanaler).containsExactlyInAnyOrder(DITT_NAV);
	}

	@Test
	public void decidesOnDittNavAndSmsWhenOnlyMobilInfoIsValidAndUsersPrefersEpostAndDittNav() {
		Collection<KanalCode> kanaler = decider.decideKanaler(kontaktMedKunMobil(), PREFERERT_KANAL_BAADE_DITTNAV_OG_EPOST);

		assertThat(kanaler).containsExactlyInAnyOrder(DITT_NAV, SMS);
	}

	@Test
	public void decidesOnDittNavAndEpostWhenOnlyEpostInfoIsValidAndUsersPrefersSmsAndDittNav() {
		Collection<KanalCode> kanaler = decider.decideKanaler(kontaktMedKunEpost(), PREFERERT_KANAL_BAADE_DITTNAV_OG_SMS);

		assertThat(kanaler).containsExactlyInAnyOrder(DITT_NAV, EPOST);
	}

	private static KontaktregisterTo kontaktMedBaadeEpostOgMobil() {
		return createKontaktTo(KONTAKTINFO_EPOST, KONTAKTINFO_MOBIL);
	}

	private static KontaktregisterTo kontaktMedKunEpost() {
		return createKontaktTo(KONTAKTINFO_EPOST, null);
	}

	private static KontaktregisterTo kontaktMedKunMobil() {
		return createKontaktTo(null, KONTAKTINFO_MOBIL);
	}

	private static KontaktregisterTo kontaktUtenEpostOgMobil() {
		return createKontaktTo(null, null);
	}

	private static KontaktregisterTo createKontaktTo(String epost, String mobil) {
		return KontaktregisterTo.builder()
				.epostadresse(epost)
				.mobiltelefonnummer(mobil)
				.build();
	}
}