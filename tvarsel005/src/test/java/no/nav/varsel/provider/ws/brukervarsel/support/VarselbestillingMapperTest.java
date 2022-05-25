package no.nav.varsel.provider.ws.brukervarsel.support;

import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Varselbestilling;
import no.nav.varsel.domain.utility.XmlGregorianConverter;
import no.nav.varsel.service.tvarsel005.to.VarselTo;
import no.nav.varsel.service.tvarsel005.to.VarselbestillingTo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit test for {@link VarselbestillingMapperTest}
 *
 * @author Lars Aune
 */
@ExtendWith(MockitoExtension.class)
public class VarselbestillingMapperTest {
	public static final String AKTOER_ID = "AKTOER_ID";
	public static final LocalDateTime BESTILLINGSTIDSPUNKT = LocalDateTime.of(2016, Month.JULY, 1, 0, 0, 0, 0);
	public static final int REVARSLING_INTERVALL = 5;
	public static final LocalDateTime SISTE_VARSEL_UTSENDELSE = LocalDateTime.of(2016, Month.JULY, 4, 0, 0, 0, 0);
	private static final XMLGregorianCalendar BESTILT = XmlGregorianConverter.toXmlGregorianCalendar(BESTILLINGSTIDSPUNKT);
	private static final XMLGregorianCalendar SISTE_VARSEL_UTSENDELSE_XML_GREGORIAN_CALENDAR =
			XmlGregorianConverter.toXmlGregorianCalendar(SISTE_VARSEL_UTSENDELSE);
	public static final String FNR = "FNR";

	@Spy
	private VarselMapper varselMapper;

	@InjectMocks
	private VarselbestillingMapper mapper;

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenParameterIsNull() {
		assertThrows(IllegalArgumentException.class, () -> mapper.map(null));
	}

	@Test
	public void shouldMapAktoerId() {
		VarselbestillingTo.Builder varselbestillingToBuilder = new VarselbestillingTo.Builder();
		List<VarselTo> varsler = new ArrayList<>();
		varsler.add(VarselMapperTest.buildVarselTo());
		varselbestillingToBuilder.
				aktoerId(AKTOER_ID).
				bestillingstidspunkt(BESTILLINGSTIDSPUNKT).
				revarslingIntervall(REVARSLING_INTERVALL).
				sisteVarselUtsendelse(SISTE_VARSEL_UTSENDELSE).
				varsler(varsler);
		VarselbestillingTo varselBestillingTo = varselbestillingToBuilder.build();

		Varselbestilling varselbestilling = mapper.map(varselBestillingTo);

		assertPersonHasNullValue(varselbestilling);
		assertAktoerHasValue(varselbestilling);
		assertRestOfCoreVarselbestilling(varselbestilling);
		assertVarsel(varselbestilling);
	}

	@Test
	public void shouldMapFnr() {
		VarselbestillingTo.Builder varselbestillingToBuilder = new VarselbestillingTo.Builder();
		List<VarselTo> varsler = new ArrayList<>();
		varsler.add(VarselMapperTest.buildVarselTo());
		varselbestillingToBuilder.
				fnr(FNR).
				bestillingstidspunkt(BESTILLINGSTIDSPUNKT).
				revarslingIntervall(REVARSLING_INTERVALL).
				sisteVarselUtsendelse(SISTE_VARSEL_UTSENDELSE).
				varsler(varsler);
		VarselbestillingTo varselBestillingTo = varselbestillingToBuilder.build();

		Varselbestilling varselbestilling = mapper.map(varselBestillingTo);

		assertPersonHasValue(varselbestilling);
		assertAktoerHasNullValue(varselbestilling);
		assertRestOfCoreVarselbestilling(varselbestilling);
	}

	public static void assertRestOfCoreVarselbestilling(Varselbestilling varselbestilling) {
		assertThat(varselbestilling.getBestilt(), is(BESTILT));
		assertThat(varselbestilling.getReVarselingsintervall(), is(REVARSLING_INTERVALL));
		assertThat(varselbestilling.getSisteVarselutsendelse(), is(SISTE_VARSEL_UTSENDELSE_XML_GREGORIAN_CALENDAR));
	}

	public static void assertAktoerHasNullValue(Varselbestilling varselbestilling) {
		assertThat(varselbestilling.getAktoerId(), nullValue());
	}

	public static void assertPersonHasValue(Varselbestilling varselbestilling) {
		assertThat(varselbestilling.getPerson().getIdent(), is(FNR));
	}

	public static void assertAktoerHasValue(Varselbestilling varselbestilling) {
		assertThat(varselbestilling.getAktoerId().getAktoerId(), is(AKTOER_ID));
	}

	public static void assertPersonHasNullValue(Varselbestilling varselbestilling) {
		assertThat(varselbestilling.getPerson(), nullValue());
	}

	public static void assertVarsel(Varselbestilling varselbestilling) {
		assertThat(varselbestilling.getVarselListe(), hasSize(1));
		VarselMapperTest.assertVarsel(varselbestilling.getVarselListe().get(0));
	}
}