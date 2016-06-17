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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
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
	public static final String VARSEL_FOR_DISTR_KANAL = "vardistkanal";
	public static final String VARSEL_KATEGORI = "varkat";
	public static final boolean INAKTIV = false;
	public static final int REVARSLING_INTERVALL = 4;
	public static final int ANTALL_REVARSLING = 2;
	public static final KanalCode PREFERERT_KANAL = KanalCode.EPOST;

	private static final String VARSLINGSTYPE = "varslingstypen";
	private static final String DOKKAT_URL = "http://nav.no/varselinfo";


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
		when(restTemplate.getForObject(DOKKAT_URL + "/{varslingstype}", VarselInfoRestTo.class, VARSLINGSTYPE))
				.thenReturn(restTo);
		VarselInfoTo mock = new VarselInfoTo();
		when(varselInfoMapper.map(restTo)).thenReturn(mock);

		VarselInfoTo varselInfoTo = varselInfoConsumer.hentVarselInfo(VARSLINGSTYPE);
		Assert.assertThat(varselInfoTo, is(mock));
	}

	public static VarselInfoTo createVarselInfoTo(String varseltype) {
		VarselInfoTo varselInfoTo = new VarselInfoTo();
		varselInfoTo.setVarslingstype(varseltype);
		varselInfoTo.setVarselForDistrKanal(VARSEL_FOR_DISTR_KANAL);
		varselInfoTo.setVarselKategori(VARSEL_KATEGORI);
		varselInfoTo.setInaktiv(INAKTIV);
		varselInfoTo.setRevarslingIntervall(REVARSLING_INTERVALL);
		varselInfoTo.setAntallRevarsling(ANTALL_REVARSLING);
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