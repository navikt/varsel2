package no.nav.varsel.provider.ws.brukervarsel.support;

import static no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerResponseTo.Builder.aHentVarselForBrukerResponseTo;
import static no.nav.varsel.service.tvarsel005.to.VarselTo.Builder.aVarselTo;
import static no.nav.varsel.service.tvarsel005.to.VarselbestillingTo.Builder.aVarselbestillingTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Brukervarsel;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Varselbestilling;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerResponse;
import no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerResponseTo;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit test for {@link VarselbestillingMapper}
 *
 * @author Lars Aune
 */
@ExtendWith(MockitoExtension.class)
public class HentVarselForBrukerResponseMapperTest {
	private static final String VARSELTYPE_ID = "VARSELTYPE_ID";
	private static final Integer REVARSLINGSINTERVALL = 5;
	private static final String KANAL = "KANAL";
	private static final String KONTAKT_INFO = "KONTAKT_INFO";
	private static final String VARSEL_TITTEL = "VARSEL_TITTEL";
	private static final String VARSEL_TEKST = "VARSEL_TEKST";
	private static final String VARSEL_URL = "VARSEL_URL";
	private static final boolean IS_REVARSEL = true;

	@Spy
	private VarselbestillingMapper varselbestillingMapper;

	@Spy
	private VarselMapper varselMapper;


	@InjectMocks
	private HentVarselForBrukerResponseMapper mapper;

	@Before
	public void onSetup() {
		varselbestillingMapper.setVarselMapper(varselMapper);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionWhenParameterIsNull() {
		mapper.map(null);
	}

	@Test
	public void shouldMapAktoerId() {
		HentVarselForBrukerResponseTo responseTo = createResponseTo(null, VarselbestillingMapperTest.AKTOER_ID);
		HentVarselForBrukerResponse response = mapper.map(responseTo);

		//assert response
		assertThat(response.getBrukervarsel(), notNullValue());
		Brukervarsel brukervarsel = response.getBrukervarsel();
		assertThat(brukervarsel.getVarselbestillingListe(), hasSize(1));
		Varselbestilling varselbestilling = brukervarsel.getVarselbestillingListe().get(0);

		//assert Varselbestiling
		VarselbestillingMapperTest.assertAktoerHasValue(varselbestilling);
		VarselbestillingMapperTest.assertPersonHasNullValue(varselbestilling);
		VarselbestillingMapperTest.assertRestOfCoreVarselbestilling(varselbestilling);
		VarselbestillingMapperTest.assertVarsel(varselbestilling);
	}

	@Test
	public void shouldMapPerson() {
		HentVarselForBrukerResponseTo responseTo = createResponseTo(VarselbestillingMapperTest.FNR, null);
		HentVarselForBrukerResponse response = mapper.map(responseTo);

		//assert response
		assertThat(response.getBrukervarsel(), notNullValue());
		Brukervarsel brukervarsel = response.getBrukervarsel();
		assertThat(brukervarsel.getVarselbestillingListe(), hasSize(1));
		Varselbestilling varselbestilling = brukervarsel.getVarselbestillingListe().get(0);

		//assert Varselbestiling
		VarselbestillingMapperTest.assertAktoerHasNullValue(varselbestilling);
		VarselbestillingMapperTest.assertPersonHasValue(varselbestilling);
		VarselbestillingMapperTest.assertRestOfCoreVarselbestilling(varselbestilling);
		VarselbestillingMapperTest.assertVarsel(varselbestilling);
	}

	private HentVarselForBrukerResponseTo createResponseTo(String fnr, String aktoer) {
		return aHentVarselForBrukerResponseTo().
				varselbestillingTos(aVarselbestillingTo().
						varseltypeId(VARSELTYPE_ID).
						fnr(fnr).
						aktoerId(aktoer).
						bestillingstidspunkt(VarselbestillingMapperTest.BESTILLINGSTIDSPUNKT).
						revarslingIntervall(REVARSLINGSINTERVALL).
						sisteVarselUtsendelse(VarselbestillingMapperTest.SISTE_VARSEL_UTSENDELSE).
						varsler(aVarselTo().
								kanal(KANAL).
								sendtTidspunkt(VarselMapperTest.SENDT_TIDSPUNKT).
								distribusjonTidspunkt(VarselMapperTest.DISTRIBUSJON_TIDSPUNKT).
								kontaktInfo(KONTAKT_INFO).
								varselTittel(VARSEL_TITTEL).
								varselTekst(VARSEL_TEKST).
								varselURL(VARSEL_URL).
								revarsel(IS_REVARSEL).
								build()).
						build()).
				build();
	}
}