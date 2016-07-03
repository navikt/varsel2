package no.nav.varsel.provider.map.support;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Varsel;
import no.nav.varsel.service.tvarsel005.to.VarselTo;
import org.junit.Test;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.time.Month;

/**
 * Unit test for DefaultVarselMapper
 *
 * @author lars Aune
 */
public class DefaultVarselMapperTest {

	public static final String KANAL = "KANAL";
	public static final LocalDateTime SENDT_TIDSPUNKT = LocalDateTime.of(2016, Month.JULY, 1, 0, 0, 0, 0);
	public static final LocalDateTime DISTRIBUSJON_TIDSPUNKT = LocalDateTime.of(2016, Month.JULY, 2, 0, 0, 0);
	public static final String KONTAKT_INFO = "KONTAKT_INFO";
	public static final String VARSEL_TITTEL = "VARSEL_TITTEL";
	public static final String VARSEL_TEKST = "VARSEL_TEKST";
	public static final String VARSEL_URL = "VARSEL_URL";
	public static final boolean REVARSEL = true;
	private static XMLGregorianCalendar DISTRIBUERT;
	private static XMLGregorianCalendar SENDT;

	static{
		SENDT = TestdataUtil.getXMLGregorianCalendar(2016, 6, 1);
		DISTRIBUERT = TestdataUtil.getXMLGregorianCalendar(2016, 6, 2);
	}

	private static final DefaultVarselMapper MAPPER = new DefaultVarselMapper();

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionWhenParameterIsNull() {
		MAPPER.map(null);
	}

	@Test
	public void shouldMap() {
		VarselTo varselTo = buildVarselTo();

		Varsel varsel = MAPPER.map(varselTo);

		assertVarsel(varsel);
	}

	public static void assertVarsel(Varsel varsel) {
		assertThat(varsel.getKanal(), is(KANAL));
		assertThat(varsel.getSendt(), is(SENDT));
		assertThat(varsel.getDistribuert(), is(DISTRIBUERT));
		assertThat(varsel.getKontaktinfo(), is(KONTAKT_INFO));
		assertThat(varsel.getVarseltittel(), is(VARSEL_TITTEL));
		assertThat(varsel.getVarseltekst(), is(VARSEL_TEKST));
		assertThat(varsel.getVarselURL(), is(VARSEL_URL));
		assertThat(varsel.isReVarsel(), is(REVARSEL));
	}

	public static VarselTo buildVarselTo() {
		VarselTo.Builder builder = new VarselTo.Builder();
		builder.
				kanal(KANAL).
				sendtTidspunkt(SENDT_TIDSPUNKT).
				distribusjonTidspunkt(DISTRIBUSJON_TIDSPUNKT).
				kontaktInfo(KONTAKT_INFO).
				varselTittel(VARSEL_TITTEL).
				varselTekst(VARSEL_TEKST).
				varselURL(VARSEL_URL).
				revarsel(REVARSEL);

		return builder.build();
	}
}