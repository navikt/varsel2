package no.nav.varsel.service.tvarsel005.to;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static no.nav.varsel.service.tvarsel005.to.VarselTo.Builder.aVarselTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class VarselToTest {

	public static final String KANAL = "KANAL";
	public static final String KONTAKT_INFO = "KONTAKT_INFO";
	public static final String VARSEL_TITTEL = "VARSEL_TITTEL";
	public static final String VARSEL_TEKST = "VARSEL_TEKST";
	public static final String VARSEL_URL = "VARSEL_URL";
	public static final LocalDateTime SENDT_TIDSPUNKT = LocalDateTime.of(2016, Month.JULY, 1, 0, 0, 0, 0);
	public static final LocalDateTime DISTRIBUSJON_TIDSPUNKT = LocalDateTime.of(2016, Month.JULY, 2, 0, 0, 0, 0);
	public static final boolean REVARSEL = true;


	@Test
	public void shouldBuild() {

		VarselTo varselTo = buildVarselTo();

		assertVarselTo(varselTo);
	}

	public static void assertVarselTo(VarselTo varselTo) {
		assertThat(varselTo.getKanal(), is(KANAL));
		assertThat(varselTo.getSendtTidspunkt(), is(SENDT_TIDSPUNKT));
		assertThat(varselTo.getDistribusjonsTidspunkt(), is(DISTRIBUSJON_TIDSPUNKT));
		assertThat(varselTo.getKontaktInfo(), is(KONTAKT_INFO));
		assertThat(varselTo.getVarselTittel(), is(VARSEL_TITTEL));
		assertThat(varselTo.getVarselTekst(), is(VARSEL_TEKST));
		assertThat(varselTo.getVarselURL(), is(VARSEL_URL));
		assertThat(varselTo.isRevarsel(), is(true));
	}

	public static VarselTo buildVarselTo() {
		return varselToBuilder().build();
	}

	public static VarselTo.Builder varselToBuilder() {
		return aVarselTo().kanal(KANAL).
				sendtTidspunkt(SENDT_TIDSPUNKT).
				distribusjonTidspunkt(DISTRIBUSJON_TIDSPUNKT).
				kontaktInfo(KONTAKT_INFO).
				varselTittel(VARSEL_TITTEL).
				varselTekst(VARSEL_TEKST).
				varselURL(VARSEL_URL).
				revarsel(REVARSEL);
	}

}