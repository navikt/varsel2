package no.nav.varsel.consumer.dokkat;

import com.google.common.collect.Sets;
import no.nav.dokkat.schemas.tkat021.VarselInfoRestTo;
import no.nav.varsel.consumer.dokkat.VarselInfoConsumer;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.consumer.dokkat.support.VarselInfoMapper;
import no.nav.varsel.consumer.dokkat.to.VarselInfoTo;
import no.nav.varsel.consumer.dokkat.to.VarselMalTo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link VarselInfoConsumer}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@ExtendWith(MockitoExtension.class)
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

	public static final String VARSEL_URL_MED_FLETTING = VARSEL_URL + "/{param}";
	public static final KanalCode VARSEL_URL_PREFERERT_KANAL = KanalCode.DITT_NAV;

	private static final String VARSELTYPE_ID = "varseltypeIden";
	private static final String DOKKAT_URL = "http://nav.no/varselinfo";

	@Mock
	private RestTemplate restTemplate;
	@Mock
	private VarselInfoMapper varselInfoMapper;

	@InjectMocks
	private VarselInfoConsumer varselInfoConsumer;

	@BeforeEach
	public void setUp() throws Exception {
		varselInfoConsumer.setVarselinfoUrl(DOKKAT_URL);
	}

	@Test
	public void shouldConsume() {
		VarselInfoRestTo restTo = new VarselInfoRestTo();
		when(restTemplate.getForObject(DOKKAT_URL + "/{varseltypeId}", VarselInfoRestTo.class, VARSELTYPE_ID))
				.thenReturn(restTo);
		VarselInfoTo mock = new VarselInfoTo();
		when(varselInfoMapper.map(restTo)).thenReturn(mock);

		VarselInfoTo varselInfoTo = varselInfoConsumer.hentVarselInfo(VARSELTYPE_ID);
		assertThat(varselInfoTo, is(mock));
	}

	@Test
	public void shouldGiveVarseltypeIdInExceptionMessageWhen404() {
		when(restTemplate.getForObject(DOKKAT_URL + "/{varseltypeId}", VarselInfoRestTo.class, VARSELTYPE_ID))
				.thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

		Exception e = Assertions.assertThrows(RuntimeException.class, () -> varselInfoConsumer.hentVarselInfo(VARSELTYPE_ID));
		Assertions.assertEquals("Could not find varseltypeId=varseltypeIden from url=http://nav.no/varselinfo/{varseltypeId}", e.getMessage());
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
		varselMalTo.setKanal(PREFERERT_KANAL);
		varselMalTo.setTittel(VARSEL_TITTEL);
		varselMalTo.setFoerstegangsTekst(FOERSTE_GANG_TEKST);
		varselMalTo.setRevarslingTekst(REVARSLING_TEKST);

		varselInfoTo.setMaler(Sets.newHashSet(varselMalTo));
		return varselInfoTo;
	}

}