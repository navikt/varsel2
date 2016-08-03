package no.nav.varsel.wsconsumer.dokkat;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import com.google.common.collect.Sets;
import no.nav.dokkat.schemas.tkat021.VarselInfoRestTo;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.wsconsumer.dokkat.support.VarselInfoMapper;
import no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo;
import no.nav.varsel.wsconsumer.dokkat.to.VarselMalTo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Unit test for {@link VarselInfoConsumer}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@RunWith(MockitoJUnitRunner.class)
public class VarselInfoConsumerTest {

	public static final String VARSEL_TITTEL = "Varsel Tittel";
	public static final String FOERSTE_GANG_TEKST = "Første gang tekst til {mottaker}";
	public static final String REVARSLING_TEKST = "Revarsling tekst til {mottaker}";
	public static final String VARSEL_FOR_DIST_KANAL = "vardistkanal";
	public static final String VARSEL_KATEGORI = "varkat";
	public static final boolean INAKTIV = false;
	public static final int REVARSLING_INTERVALL = 4;
	public static final int ANTALL_REVARSLING = 2;
	public static final KanalCode PREFERERT_KANAL = KanalCode.EPOST;
	public static final String VARSEL_NAVN = "varselnavn";
	public static final String VARSEL_URL = "http://nav.no";

	public static final String FOERSTE_GANG_TEKST_VARSEL_URL = "test tekst {varselUrl}";
	public static final String REVARSLING_TEKST_VARSEL_URL = "varsel_varselUrl";

	private static final String VARSELTYPE_ID = "varseltypeIden";
	private static final String DOKKAT_URL = "http://nav.no/varselinfo";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Mock
	private RestTemplate restTemplate;
	@Mock
	private VarselInfoMapper varselInfoMapper;

	@InjectMocks
	private VarselInfoConsumer varselInfoConsumer;

	@Before
	public void setUp() throws Exception {
		varselInfoConsumer.setVarselinfoUrl(DOKKAT_URL);
	}

	@Test
	public void shouldConsume() throws Exception {
		VarselInfoRestTo restTo = new VarselInfoRestTo();
		when(restTemplate.getForObject(DOKKAT_URL + "/{varseltypeId}", VarselInfoRestTo.class, VARSELTYPE_ID))
				.thenReturn(restTo);
		VarselInfoTo mock = new VarselInfoTo();
		when(varselInfoMapper.map(restTo)).thenReturn(mock);

		VarselInfoTo varselInfoTo = varselInfoConsumer.hentVarselInfo(VARSELTYPE_ID);
		Assert.assertThat(varselInfoTo, is(mock));
	}

	@Test
	public void shouldGiveVarseltypeIdInExceptionMessageWhen404() throws Exception {
		when(restTemplate.getForObject(DOKKAT_URL + "/{varseltypeId}", VarselInfoRestTo.class, VARSELTYPE_ID))
				.thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

		expectedException.expect(RuntimeException.class);
		expectedException.expectMessage("Could not find varseltypeId=varseltypeIden from url=http://nav.no/varselinfo/{varseltypeId}");

		varselInfoConsumer.hentVarselInfo(VARSELTYPE_ID);
	}

	public static VarselInfoTo createVarselInfoTo(String varseltype) {
		VarselInfoTo varselInfoTo = new VarselInfoTo();
		varselInfoTo.setVarseltypeId(varseltype);
		varselInfoTo.setVarselNavn(VARSEL_NAVN);
		varselInfoTo.setVarselForDistKanal(VARSEL_FOR_DIST_KANAL);
		varselInfoTo.setVarselKategori(VARSEL_KATEGORI);
		varselInfoTo.setInaktiv(INAKTIV);
		varselInfoTo.setRevarslingIntervall(REVARSLING_INTERVALL);
		varselInfoTo.setAntallRevarsling(ANTALL_REVARSLING);
		varselInfoTo.setVarselUrl(VARSEL_URL);
		varselInfoTo.addPreferertKanal(PREFERERT_KANAL);

		VarselMalTo varselMalTo = new VarselMalTo();
		varselMalTo.setKanal(KanalCode.EPOST);
		varselMalTo.setTittel(VARSEL_TITTEL);
		varselMalTo.setFoerstegangsTekst(FOERSTE_GANG_TEKST);
		varselMalTo.setRevarslingTekst(REVARSLING_TEKST);

		varselInfoTo.setMaler(Sets.newHashSet(varselMalTo));
		return varselInfoTo;
	}

}