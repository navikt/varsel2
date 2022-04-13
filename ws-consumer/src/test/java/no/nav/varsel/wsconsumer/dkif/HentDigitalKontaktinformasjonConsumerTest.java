package no.nav.varsel.wsconsumer.dkif;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.DigitalKontaktinformasjonV1;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.HentDigitalKontaktinformasjonKontaktinformasjonIkkeFunnet;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.HentDigitalKontaktinformasjonPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.binding.HentDigitalKontaktinformasjonSikkerhetsbegrensing;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.feil.KontaktinformasjonIkkeFunnet;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.feil.PersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.feil.Sikkerhetsbegrensing;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.HentDigitalKontaktinformasjonRequest;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.HentDigitalKontaktinformasjonResponse;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.wsconsumer.dkif.support.HentDigitalKontaktinformasjonMapper;
import no.nav.varsel.wsconsumer.dkif.to.KontaktregisterTo;
import no.nav.varsel.wsconsumer.support.VarselKanalDecider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Set;

import static no.nav.varsel.domain.code.KanalCode.DITT_NAV;
import static no.nav.varsel.domain.code.KanalCode.SMS;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link HentDigitalKontaktinformasjonConsumer}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@ExtendWith(MockitoExtension.class)
public class HentDigitalKontaktinformasjonConsumerTest {

	private static final String ID_OK = "ok";
	private static final String ID_404 = "404";
	private static final String ID_KON404 = "4042";
	private static final String ID_500 = "500";
	public static final Set<KanalCode> PREFERERT_KANAL = Sets.newHashSet(DITT_NAV);
	public static final Set<KanalCode> PREFERERT_KANAL_2 = Sets.newHashSet(SMS);

	@Mock
	private VarselKanalDecider varselKanalDecider;
	@Mock
	private DigitalKontaktinformasjonV1 digitalKontaktinformasjonV1;
	@Mock
	private HentDigitalKontaktinformasjonMapper mapper;

	@InjectMocks
	private HentDigitalKontaktinformasjonConsumer consumer;
	private KontaktregisterTo kontaktregisterTo;
	private HentDigitalKontaktinformasjonResponse response;
	private final ArrayList<KanalCode> kanalCodes = Lists.newArrayList(DITT_NAV);
	private final ArrayList<KanalCode> kanalCodes2 = Lists.newArrayList(SMS);

	@BeforeEach
	public void setUp() throws Exception {
		kontaktregisterTo = new KontaktregisterTo();
		response = new HentDigitalKontaktinformasjonResponse();
	}

	@Test
	public void shouldHentDigitalKontaktinfo() throws Exception {
		when(digitalKontaktinformasjonV1.hentDigitalKontaktinformasjon(reqId(ID_OK))).thenReturn(response);
		when(mapper.map(response)).thenReturn(kontaktregisterTo);

		KontaktregisterTo kontaktregisterTo = consumer.hentDigitalKontaktinformasjon(ID_OK);

		assertThat(kontaktregisterTo, is(this.kontaktregisterTo));
	}

	@Test
	public void shouldHentDigitalKontaktinfoAndDecideKanaler() throws Exception {
		when(digitalKontaktinformasjonV1.hentDigitalKontaktinformasjon(reqId(ID_OK))).thenReturn(response);
		when(mapper.map(response)).thenReturn(kontaktregisterTo);
		when(varselKanalDecider.decideKanaler(kontaktregisterTo, PREFERERT_KANAL)).thenReturn(kanalCodes);

		KontaktregisterTo kontaktregisterTo = consumer.hentDigitalKontaktinformasjonAndDecideKanal(ID_OK, PREFERERT_KANAL);

		assertThat(kontaktregisterTo.getKanaler(), is(kanalCodes));
	}

	@Test
	public void shouldHentDigitalKontaktinfoAndDecideKanalerWhenNullResponseFromDKif() throws Exception {
		when(digitalKontaktinformasjonV1.hentDigitalKontaktinformasjon(reqId(ID_404)))
				.thenThrow(new HentDigitalKontaktinformasjonPersonIkkeFunnet("", new PersonIkkeFunnet()));
		when(varselKanalDecider.decideKanaler(any(KontaktregisterTo.class), eq(PREFERERT_KANAL_2))).thenReturn(kanalCodes2);

		KontaktregisterTo kontaktregisterTo = consumer.hentDigitalKontaktinformasjonAndDecideKanal(ID_404, PREFERERT_KANAL_2);

		assertThat(kontaktregisterTo.getKanaler(), is(kanalCodes2));
	}

	@Test
	public void shouldReturnNullOn_NotFound() throws Exception {
		when(digitalKontaktinformasjonV1.hentDigitalKontaktinformasjon(reqId(ID_404)))
				.thenThrow(new HentDigitalKontaktinformasjonPersonIkkeFunnet("", new PersonIkkeFunnet()));

		KontaktregisterTo actual = consumer.hentDigitalKontaktinformasjon(ID_404);

		assertKontaktRegisterToIsEmpty(actual);
	}

	@Test
	public void shouldReturnNullOn_NotFoundKontaktInfo() throws Exception {
		when(digitalKontaktinformasjonV1.hentDigitalKontaktinformasjon(reqId(ID_KON404)))
				.thenThrow(new HentDigitalKontaktinformasjonKontaktinformasjonIkkeFunnet("", new KontaktinformasjonIkkeFunnet()));

		KontaktregisterTo actual = consumer.hentDigitalKontaktinformasjon(ID_KON404);

		assertKontaktRegisterToIsEmpty(actual);
	}

	@Test
	public void shouldReturnNullOn_Sikkerhetsbegrensning() throws Exception {
		when(digitalKontaktinformasjonV1.hentDigitalKontaktinformasjon(reqId(ID_500)))
				.thenThrow(new HentDigitalKontaktinformasjonSikkerhetsbegrensing("", new Sikkerhetsbegrensing()));

		KontaktregisterTo actual = consumer.hentDigitalKontaktinformasjon(ID_500);

		assertKontaktRegisterToIsEmpty(actual);
	}

	private void assertKontaktRegisterToIsEmpty(KontaktregisterTo actual) {
		assertThat(actual.getKanaler(), nullValue());
		assertThat(actual.getEpostadresse(), nullValue());
		assertThat(actual.getMobiltelefonnummer(), nullValue());
	}

	private HentDigitalKontaktinformasjonRequest reqId(String ident) {
		HentDigitalKontaktinformasjonRequest request = new HentDigitalKontaktinformasjonRequest() {
			@Override
			public boolean equals(Object obj) {
				return obj instanceof HentDigitalKontaktinformasjonRequest &&
						this.getPersonident().equals(((HentDigitalKontaktinformasjonRequest) obj).getPersonident());
			}
		};
		request.setPersonident(ident);
		return eq(request);
	}
}