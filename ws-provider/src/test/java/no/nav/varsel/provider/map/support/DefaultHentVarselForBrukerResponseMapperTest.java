package no.nav.varsel.provider.map.support;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Brukervarsel;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Varselbestilling;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerResponse;
import no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerResponseTo;
import no.nav.varsel.service.tvarsel005.to.VarselTo;
import no.nav.varsel.service.tvarsel005.to.VarselbestillingTo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lars Aune
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultHentVarselForBrukerResponseMapperTest {
	private static final String VARSELTYPE_ID = "VARSELTYPE_ID";
	private static final Integer REVARSLINGSINTERVALL = 5;
	public static final String KANAL = "KANAL";
	public static final String KONTAKT_INFO = "KONTAKT_INFO";
	public static final String VARSEL_TITTEL = "VARSEL_TITTEL";
	public static final String VARSEL_TEKST = "VARSEL_TEKST";
	public static final String VARSEL_URL = "VARSEL_URL";
	private static final boolean IS_REVARSEL = true;

	@Spy
	private DefaultVarselbestillingMapper varselbestillingMapper;

	@Spy
	private DefaultVarselMapper varselMapper;


	@InjectMocks
	private DefaultHentVarselForBrukerResponseMapper mapper;

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
		HentVarselForBrukerResponseTo.Builder responseToBuilder = new HentVarselForBrukerResponseTo.Builder();
		List<VarselbestillingTo> brukersVarsler = new ArrayList<>();
		VarselbestillingTo.Builder varselbestillingToBuilder = new VarselbestillingTo.Builder();
		List<VarselTo> varsler = new ArrayList<>();
		VarselTo.Builder varselToBuilder = new VarselTo.Builder();

		varselToBuilder.
				kanal(KANAL).
				sendtTidspunkt(DefaultVarselMapperTest.SENDT_TIDSPUNKT).
				distribusjonTidspunkt(DefaultVarselMapperTest.DISTRIBUSJON_TIDSPUNKT).
				kontaktInfo(KONTAKT_INFO).
				varselTittel(VARSEL_TITTEL).
				varselTekst(VARSEL_TEKST).
				varselURL(VARSEL_URL).
				revarsel(IS_REVARSEL);

		varsler.add(varselToBuilder.build());

		varselbestillingToBuilder.
				varseltypeId(VARSELTYPE_ID).
				aktoerId(DefaultVarselbestillingMapperTest.AKTOER_ID).
				bestillingstidspunkt(DefaultVarselbestillingMapperTest.BESTILLINGSTIDSPUNKT).
				revarslingIntervall(REVARSLINGSINTERVALL).
				sisteVarselUtsendelse(DefaultVarselbestillingMapperTest.SISTE_VARSEL_UTSENDELSE).
				varsler(varsler);

		brukersVarsler.add(varselbestillingToBuilder.build());
		responseToBuilder.brukersVarsler(brukersVarsler);
		HentVarselForBrukerResponseTo responseTo = responseToBuilder.build();

		HentVarselForBrukerResponse response = mapper.map(responseTo);

		//assert response
		assertThat(response.getBrukervarsel(), notNullValue());
		Brukervarsel brukervarsel = response.getBrukervarsel();
		assertThat(brukervarsel.getVarselbestillingListe(), hasSize(1));
		Varselbestilling varselbestilling = brukervarsel.getVarselbestillingListe().get(0);

		//assert Varselbestiling
		DefaultVarselbestillingMapperTest.assertAktoerHasValue(varselbestilling);
		DefaultVarselbestillingMapperTest.assertPersonHasNullValue(varselbestilling);
		DefaultVarselbestillingMapperTest.assertRestOfCoreVarselbestilling(varselbestilling);
		DefaultVarselbestillingMapperTest.assertVarsel(varselbestilling);
	}

	@Test
	public void shouldMapPersom() {
		HentVarselForBrukerResponseTo.Builder responseToBuilder = new HentVarselForBrukerResponseTo.Builder();
		List<VarselbestillingTo> brukersVarsler = new ArrayList<>();
		VarselbestillingTo.Builder varselbestillingToBuilder = new VarselbestillingTo.Builder();
		List<VarselTo> varsler = new ArrayList<>();
		VarselTo.Builder varselToBuilder = new VarselTo.Builder();

		varselToBuilder.
				kanal(KANAL).
				sendtTidspunkt(DefaultVarselMapperTest.SENDT_TIDSPUNKT).
				distribusjonTidspunkt(DefaultVarselMapperTest.DISTRIBUSJON_TIDSPUNKT).
				kontaktInfo(KONTAKT_INFO).
				varselTittel(VARSEL_TITTEL).
				varselTekst(VARSEL_TEKST).
				varselURL(VARSEL_URL).
				revarsel(IS_REVARSEL);

		varsler.add(varselToBuilder.build());

		varselbestillingToBuilder.
				varseltypeId(VARSELTYPE_ID).
				fnr(DefaultVarselbestillingMapperTest.FNR).
				bestillingstidspunkt(DefaultVarselbestillingMapperTest.BESTILLINGSTIDSPUNKT).
				revarslingIntervall(REVARSLINGSINTERVALL).
				sisteVarselUtsendelse(DefaultVarselbestillingMapperTest.SISTE_VARSEL_UTSENDELSE).
				varsler(varsler);

		brukersVarsler.add(varselbestillingToBuilder.build());
		responseToBuilder.brukersVarsler(brukersVarsler);
		HentVarselForBrukerResponseTo responseTo = responseToBuilder.build();

		HentVarselForBrukerResponse response = mapper.map(responseTo);

		//assert response
		assertThat(response.getBrukervarsel(), notNullValue());
		Brukervarsel brukervarsel = response.getBrukervarsel();
		assertThat(brukervarsel.getVarselbestillingListe(), hasSize(1));
		Varselbestilling varselbestilling = brukervarsel.getVarselbestillingListe().get(0);

		//assert Varselbestiling
		DefaultVarselbestillingMapperTest.assertAktoerHasNullValue(varselbestilling);
		DefaultVarselbestillingMapperTest.assertPersonHasValue(varselbestilling);
		DefaultVarselbestillingMapperTest.assertRestOfCoreVarselbestilling(varselbestilling);
		DefaultVarselbestillingMapperTest.assertVarsel(varselbestilling);
	}
}