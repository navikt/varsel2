package no.nav.varsel.provider.ws.brukervarsel.support;

import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Varsel;
import no.nav.varsel.service.tvarsel005.to.VarselTo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.time.Month;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class VarselMapperTest {

	private static final String KANAL = "KANAL";
	static final LocalDateTime SENDT_TIDSPUNKT = LocalDateTime.of(2016, Month.JULY, 1, 0, 0, 0, 0);
	static final LocalDateTime DISTRIBUSJON_TIDSPUNKT = LocalDateTime.of(2016, Month.JULY, 2, 0, 0, 0);
	private static final String KONTAKT_INFO = "KONTAKT_INFO";
	private static final String VARSEL_TITTEL = "VARSEL_TITTEL";
	private static final String VARSEL_TEKST = "VARSEL_TEKST";
	private static final String VARSEL_URL = "VARSEL_URL";
	private static final boolean REVARSEL = true;
	private static final XMLGregorianCalendar DISTRIBUERT;
	private static final XMLGregorianCalendar SENDT;

	static {
		SENDT = TestdataUtil.getXMLGregorianCalendar(2016, 6, 1);
		DISTRIBUERT = TestdataUtil.getXMLGregorianCalendar(2016, 6, 2);
	}

	private static final VarselMapper MAPPER = new VarselMapper();

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenParameterIsNull() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> MAPPER.map(null));
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