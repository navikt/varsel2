package no.nav.varsel.consumer.support;

import no.nav.varsel.consumer.dkif.to.KontaktregisterTo;
import no.nav.varsel.domain.code.KanalCode;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Set;

import static no.nav.varsel.domain.code.KanalCode.DITT_NAV;
import static no.nav.varsel.domain.code.KanalCode.EPOST;
import static no.nav.varsel.domain.code.KanalCode.SMS;
import static org.assertj.core.api.Assertions.assertThat;

public class VarselKanalDeciderTest {

	private static final String KONTAKTINFO_EPOST = "epost@epost.no";
	private static final String KONTAKTINFO_MOBIL = "12345678";

	private static final Set<KanalCode> PREFERERT_KANAL_KUN_SMS = EnumSet.of(SMS);
	private static final Set<KanalCode> PREFERERT_KANAL_KUN_EPOST = EnumSet.of(EPOST);
	private static final Set<KanalCode> PREFERERT_KANAL_KUN_DITTNAV = EnumSet.of(DITT_NAV);
	private static final Set<KanalCode> PREFERERT_KANAL_BAADE_SMS_OG_EPOST = EnumSet.of(SMS, EPOST);
	private static final Set<KanalCode> PREFERERT_KANAL_BAADE_DITTNAV_OG_EPOST = EnumSet.of(DITT_NAV, EPOST);
	private static final Set<KanalCode> PREFERERT_KANAL_BAADE_DITTNAV_OG_SMS = EnumSet.of(DITT_NAV, SMS);
	private static final Set<KanalCode> PREFERERT_KANAL_ALLE = EnumSet.of(SMS, EPOST, DITT_NAV);

	private final VarselKanalDecider decider = new VarselKanalDecider();

	@Test
	public void shouldPrefDittNav() {
		Set<KanalCode> kanaler = decider.decideKanaler(kontaktMedBaadeEpostOgMobil(), PREFERERT_KANAL_KUN_DITTNAV);

		assertThat(kanaler).containsExactly(DITT_NAV);
	}

	@Test
	public void shouldPrefAll() {
		Set<KanalCode> kanaler = decider.decideKanaler(kontaktMedBaadeEpostOgMobil(), PREFERERT_KANAL_ALLE);

		assertThat(kanaler).containsExactlyInAnyOrder(SMS, EPOST, DITT_NAV);
	}

	@Test
	public void shouldPrefSmsEpost() {
		Set<KanalCode> kanaler = decider.decideKanaler(kontaktMedBaadeEpostOgMobil(), PREFERERT_KANAL_BAADE_SMS_OG_EPOST);

		assertThat(kanaler).containsExactlyInAnyOrder(SMS, EPOST);
	}

	@Test
	public void shouldPrefSmsEpostOnlyEpost() {
		Set<KanalCode> kanaler = decider.decideKanaler(kontaktMedKunEpost(), PREFERERT_KANAL_BAADE_SMS_OG_EPOST);

		assertThat(kanaler).containsExactly(EPOST);
	}

	@Test
	public void shouldPrefSmsEpostOnlySms() {
		Set<KanalCode> kanaler = decider.decideKanaler(kontaktMedKunMobil(), PREFERERT_KANAL_BAADE_SMS_OG_EPOST);

		assertThat(kanaler).containsExactly(SMS);
	}

	@Test
	public void shouldPrefSmsEpostFallbackDittNav() {
		Set<KanalCode> kanaler = decider.decideKanaler(kontaktUtenEpostOgMobil(), PREFERERT_KANAL_BAADE_SMS_OG_EPOST);

		assertThat(kanaler).containsExactly(DITT_NAV);
	}

	@Test
	public void shouldPrefSmsEpostOnNull() {
		Set<KanalCode> kanaler = decider.decideKanaler(kontaktMedBaadeEpostOgMobil(), null);

		assertThat(kanaler).containsExactlyInAnyOrder(SMS, EPOST);
	}

	@Test
	public void shouldPrefEpostOnNullOnlyEpost() {
		Set<KanalCode> kanaler = decider.decideKanaler(kontaktMedKunEpost(), null);

		assertThat(kanaler).containsExactly(EPOST);
	}

	@Test
	public void shouldPrefSmsOnNullOnlySms() {
		Set<KanalCode> kanaler = decider.decideKanaler(kontaktMedKunMobil(), null);

		assertThat(kanaler).containsExactly(SMS);
	}

	@Test
	public void shouldPrefSms() {
		Set<KanalCode> kanaler = decider.decideKanaler(kontaktMedBaadeEpostOgMobil(), PREFERERT_KANAL_KUN_SMS);

		assertThat(kanaler).containsExactly(SMS);
	}

	@Test
	public void shouldPrefSmsFallbackEpost() {
		Set<KanalCode> kanaler = decider.decideKanaler(kontaktMedKunEpost(), PREFERERT_KANAL_KUN_SMS);

		assertThat(kanaler).containsExactly(EPOST);
	}

	@Test
	public void shouldPrefSmsFinalFallbackDittNav() {
		Set<KanalCode> kanaler = decider.decideKanaler(kontaktUtenEpostOgMobil(), PREFERERT_KANAL_KUN_SMS);

		assertThat(kanaler).containsExactly(DITT_NAV);
	}

	@Test
	public void shouldPrefEpost() {
		Set<KanalCode> kanaler = decider.decideKanaler(kontaktMedBaadeEpostOgMobil(), PREFERERT_KANAL_KUN_EPOST);

		assertThat(kanaler).containsExactly(EPOST);
	}

	@Test
	public void shouldPrefEpostFallbackSms() {
		Set<KanalCode> kanaler = decider.decideKanaler(kontaktMedKunMobil(), PREFERERT_KANAL_KUN_EPOST);

		assertThat(kanaler).containsExactly(SMS);
	}

	@Test
	public void shouldPrefEpostFinalFallbackDittNav() {
		Set<KanalCode> kanaler = decider.decideKanaler(kontaktUtenEpostOgMobil(), PREFERERT_KANAL_KUN_EPOST);

		assertThat(kanaler).containsExactly(DITT_NAV);
	}

	@Test
	public void decidesOnDittNavAndSmsWhenOnlyMobilInfoIsValidAndUsersPrefersEpostAndDittNav() {
		Set<KanalCode> kanaler = decider.decideKanaler(kontaktMedKunMobil(), PREFERERT_KANAL_BAADE_DITTNAV_OG_EPOST);

		assertThat(kanaler).containsExactlyInAnyOrder(DITT_NAV, SMS);
	}

	@Test
	public void decidesOnDittNavAndEpostWhenOnlyEpostInfoIsValidAndUsersPrefersSmsAndDittNav() {
		Set<KanalCode> kanaler = decider.decideKanaler(kontaktMedKunEpost(), PREFERERT_KANAL_BAADE_DITTNAV_OG_SMS);

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