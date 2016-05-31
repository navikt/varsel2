package no.nav.varsel.service;

import static no.nav.varsel.service.VarslelKanalDecider.SMS_EPOST;
import static no.nav.varsel.wsconsumer.dkif.to.KontaktregisterTo.KontaktregisterToBuilder.aKontaktregisterTo;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.wsconsumer.dkif.to.KontaktregisterTo;
import org.junit.Test;

import java.util.Collection;

/**
 * Unit test for {@link VarslelKanalDecider}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarslelKanalDeciderTest {

	private static final String EPOST = "epost@epost.no";
	private static final String MOBIL = "12345678";

	private VarslelKanalDecider decider = new VarslelKanalDecider();

	@Test
	public void shouldPrefDittNav() throws Exception {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(EPOST, MOBIL), KanalCode.DITTNAV.toString());
		assertThat(kanaler, containsInAnyOrder(KanalCode.DITTNAV));
	}

	@Test
	public void shouldPrefSmsEpost() throws Exception {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(EPOST, MOBIL), SMS_EPOST);
		assertThat(kanaler, containsInAnyOrder(KanalCode.SMS, KanalCode.EPOST));
	}

	@Test
	public void shouldPrefSmsEpostOnlyEpost() throws Exception {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(EPOST, null), SMS_EPOST);
		assertThat(kanaler, containsInAnyOrder(KanalCode.EPOST));
	}

	@Test
	public void shouldPrefSmsEpostOnlySms() throws Exception {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(null, MOBIL), SMS_EPOST);
		assertThat(kanaler, containsInAnyOrder(KanalCode.SMS));
	}

	@Test
	public void shouldPrefSmsEpostFallbackDittNav() throws Exception {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(null, null), SMS_EPOST);
		assertThat(kanaler, containsInAnyOrder(KanalCode.DITTNAV));
	}

	@Test
	public void shouldPrefSmsEpostOnNull() throws Exception {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(EPOST, MOBIL), null);
		assertThat(kanaler, containsInAnyOrder(KanalCode.SMS, KanalCode.EPOST));
	}


	@Test
	public void shouldPrefSms() throws Exception {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(EPOST, MOBIL), KanalCode.SMS.toString());
		assertThat(kanaler, containsInAnyOrder(KanalCode.SMS));
	}

	@Test
	public void shouldPrefSmsFallbackEpost() throws Exception {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(EPOST, null), KanalCode.SMS.toString());
		assertThat(kanaler, containsInAnyOrder(KanalCode.EPOST));
	}

	@Test
	public void shouldPrefSmsFinalFallbackDittNav() throws Exception {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(null, null), KanalCode.SMS.toString());
		assertThat(kanaler, containsInAnyOrder(KanalCode.DITTNAV));
	}

	@Test
	public void shouldPrefEpost() throws Exception {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(EPOST, MOBIL), KanalCode.EPOST.toString());
		assertThat(kanaler, containsInAnyOrder(KanalCode.EPOST));
	}

	@Test
	public void shouldPrefEpostFallbackSms() throws Exception {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(null, MOBIL), KanalCode.EPOST.toString());
		assertThat(kanaler, containsInAnyOrder(KanalCode.SMS));
	}

	@Test
	public void shouldPrefEpostFinalFallbackDittNav() throws Exception {
		Collection<KanalCode> kanaler = decider.decideKanaler(createKontaktTo(null, null), KanalCode.EPOST.toString());
		assertThat(kanaler, containsInAnyOrder(KanalCode.DITTNAV));
	}

	private static KontaktregisterTo createKontaktTo(String epost, String mobil) {
		return aKontaktregisterTo()
				.epostadresse(epost)
				.mobiltelefonnummer(mobil)
				.build();
	}

}