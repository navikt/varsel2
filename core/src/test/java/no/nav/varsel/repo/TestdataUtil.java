package no.nav.varsel.repo;

import com.google.common.collect.Sets;
import no.nav.varsel.domain.builder.VarselBuilder;
import no.nav.varsel.domain.builder.VarselbestillingBuilder;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.code.StatusCode;
import no.nav.varsel.domain.object.Varsel;
import no.nav.varsel.domain.object.Varselbestilling;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static no.nav.varsel.domain.builder.VarselBuilder.aVarsel;
import static no.nav.varsel.domain.builder.VarselbestillingBuilder.aVarselbestilling;

public class TestdataUtil {

	public static final String VARSELBESTILLING_ID = "d9f8f75e-13cb-4766-81c8-306d9d9385b2";
	public static final String VARSELTYPE_ID = "UT";
	public static final String EPOST = "test@test.no";
	public static final Set<KanalCode> PREFERERT_KANAL = Sets.newHashSet(KanalCode.EPOST);
	public static final Set<KanalCode> PREFERERT_KANAL_MED_DITT_NAV = Sets.newHashSet(KanalCode.EPOST, KanalCode.DITT_NAV);
	public static final LocalDateTime UTLOP_TIDSPUNKT = LocalDateTime.now().plusHours(1);
	public static final String FNR = "11112222333";
	public static final String AKTOR_ID = "1111222233334444";
	public static final LocalDateTime BESTILLING_TIDSPUNKT = LocalDateTime.parse("2016-04-04T11:12:13");
	public static final int REVARSLING_INTERVALL = 7;
	public static final int ANTALL_REVARSLINGER = 2;
	public static final LocalDate NESTE_VARSLING_DATO = LocalDate.parse("2016-04-05");
	public static final String VARSEL_ID = "fc763632-40b0-4504-a7d1-8c44ee199b11";
	public static final KanalCode KANAL_CODE = KanalCode.EPOST;
	public static final LocalDateTime SENDT_TIDSPUNKT = LocalDateTime.parse("2016-04-05T14:15:16");
	public static final LocalDateTime DISTRIBUSJON_TIDSPUNKT = LocalDateTime.parse("2016-04-03T04:05:06");
	public static final LocalDateTime KVITTERING_TIDSPUNKT = LocalDateTime.parse("2016-04-06T04:55:06");
	public static final String KONTAKT_INFO = "Kontakt Informasjon om Bruker";
	public static final StatusCode STATUS_CODE = StatusCode.FERDIGBEHANDLET;
	public static final String FEILBESKRIVELSE = "ikke veldig feil";
	public static final String VARSEL_TITTEL = "Du har fått svar på din søknad om rosa takvifte";
	public static final String VARSEL_TEKST = "Sjekk Ditt NAV på www.nav.no for å se hva som har blitt vedtatt " +
			"anngående din søknad om :antall takvifter.";
	public static final String VARSEL_URL = "http://www.nav.no/dittnav/takvifte/rosa/1212aeg23g";
	public static final String PARAMETERKEY = "antall";
	public static final String PARAMETERVALUE = "17";
	public static final boolean ER_REVARSEL = false;

	/**
	 * Create varselbestilling with preset values
	 */
	public static Varselbestilling createVarselbestilling() {
		return createVarselbestillingBuilder()
				.varselbestillingId(VARSELBESTILLING_ID)
				.varsels(createVarsel())
				.build();
	}

	/**
	 * create a builder where unique Id are set at random
	 */
	public static VarselbestillingBuilder createVarselbestillingBuilder() {
		return aVarselbestilling()
				.varselbestillingId(UUID.randomUUID().toString())
				.varseltypeId(VARSELTYPE_ID)
				.utlopTidspunkt(UTLOP_TIDSPUNKT)
				.fnr(FNR)
				.aktorId(AKTOR_ID)
				.bestillingTidspunkt(BESTILLING_TIDSPUNKT)
				.revarslingIntervall(REVARSLING_INTERVALL)
				.antallRevarslinger(ANTALL_REVARSLINGER)
				.nesteVarslingDato(NESTE_VARSLING_DATO)
				.parameter(PARAMETERKEY, PARAMETERVALUE)
				.varsels(createVarselUnique());
	}

	/**
	 * Create a varsel with test values
	 */
	public static Varsel createVarsel() {
		return createVarselBuilder()
				.varselId(VARSEL_ID)
				.build();
	}

	/**
	 * Create a varsel with unique Id
	 */
	public static Varsel createVarselUnique() {
		return createVarselBuilder()
				.build();
	}

	/**
	 * create a builder where unique Id are set at random
	 */
	public static VarselBuilder createVarselBuilder() {
		return aVarsel()
				.varselId(UUID.randomUUID().toString())
				.kanal(KANAL_CODE)
				.sendtTidspunkt(SENDT_TIDSPUNKT)
				.distribusjonTidspunkt(DISTRIBUSJON_TIDSPUNKT)
				.kvitteringTidspunkt(KVITTERING_TIDSPUNKT)
				.kontaktInfo(KONTAKT_INFO)
				.status(STATUS_CODE)
				.feilbeskrivelse(FEILBESKRIVELSE)
				.varselTittel(VARSEL_TITTEL)
				.varselTekst(VARSEL_TEKST)
				.varselUrl(VARSEL_URL)
				.erRevarsel(ER_REVARSEL);
	}
}
