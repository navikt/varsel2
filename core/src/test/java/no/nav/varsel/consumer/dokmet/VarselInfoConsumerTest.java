package no.nav.varsel.consumer.dokmet;

import no.nav.dokkat.schemas.tkat021.VarselInfoRestTo;
import no.nav.varsel.consumer.dokmet.support.VarselInfoMapper;
import no.nav.varsel.consumer.dokmet.to.VarselInfoTo;
import no.nav.varsel.domain.code.KanalCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;

@SpringBootTest(classes = {VarselInfoConsumer.class})
@ActiveProfiles({"itest"})
public class VarselInfoConsumerTest {

	public static final String VARSEL_TITTEL = "Varsel Tittel";
	public static final String FOERSTE_GANG_TEKST = "Første gang tekst til {mottaker}";
	public static final String REVARSLING_TEKST = "Revarsling tekst til {mottaker}";
	public static final String VARSEL_FOR_DIST_KANAL = "vardistkanal";
	public static final String VARSEL_KATEGORI = "varkat";
	public static final boolean INAKTIV = false;
	public static final int REVARSLING_INTERVALL = 4;
	public static final KanalCode PREFERERT_KANAL = KanalCode.EPOST;
	public static final String VARSEL_NAVN = "varselnavn";
	public static final String VARSEL_URL = "http://nav.no";

	private static final String VARSELTYPE_ID = "varseltypeIden";

	@MockBean
	private RestTemplate restTemplate;
	@MockBean
	private VarselInfoMapper varselInfoMapper;

	@Autowired
	private VarselInfoConsumer varselInfoConsumer;

	@Test
	public void shouldConsume() {
		VarselInfoRestTo varselInfoRestTo = new VarselInfoRestTo();
		ResponseEntity<VarselInfoRestTo> response = new ResponseEntity<>(varselInfoRestTo, HttpStatus.OK);
		when(restTemplate.exchange(anyString(), eq(GET), any(HttpEntity.class), eq(VarselInfoRestTo.class), anyString()))
				.thenReturn(response);

		VarselInfoTo mock = new VarselInfoTo();
		when(varselInfoMapper.map(varselInfoRestTo)).thenReturn(mock);

		VarselInfoTo varselInfoTo = varselInfoConsumer.hentVarselInfo(VARSELTYPE_ID);
		assertThat(varselInfoTo, is(mock));
	}

	@Test
	public void shouldGiveVarseltypeIdInExceptionMessageWhen404() {
		when(restTemplate.exchange(anyString(), eq(GET), any(HttpEntity.class), eq(VarselInfoRestTo.class), anyString()))
				.thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

		Exception e = Assertions.assertThrows(RuntimeException.class, () -> varselInfoConsumer.hentVarselInfo(VARSELTYPE_ID));
		Assertions.assertTrue(e.getMessage().contains("Could not find varseltypeId=varseltypeIden from url="));
	}
}