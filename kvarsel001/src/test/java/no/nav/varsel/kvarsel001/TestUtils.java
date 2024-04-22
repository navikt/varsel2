package no.nav.varsel.kvarsel001;

import no.nav.doknotifikasjon.schemas.DoknotifikasjonStatus;
import no.nav.varsel.domain.builder.VarselBuilder;
import no.nav.varsel.domain.builder.VarselbestillingBuilder;
import no.nav.varsel.domain.object.Varsel;
import no.nav.varsel.domain.object.Varselbestilling;

import java.time.LocalDateTime;
import java.util.UUID;

import static no.nav.varsel.domain.code.KanalCode.EPOST;
import static no.nav.varsel.domain.code.StatusCode.SENDT;

public class TestUtils {

	private static final String BESTILLINGSID = UUID.randomUUID().toString();
	private static final String BESTILLERID = "varsel";
	private static final String STATUS = "FERDIGSTILT";
	private static final String MELDING = "En melding";
	private static final Long DISTRIBUSJONID = null;

	private static final String VARSELTYPEID = "Gruppeaktivitet";
	private static final String FNR = "12345678910";
	private static final String AKTORID = "23456789101";

	public static DoknotifikasjonStatus createDoknotifikasjonStatus() {
		return DoknotifikasjonStatus.newBuilder()
				.setBestillingsId(BESTILLINGSID)
				.setBestillerId(BESTILLERID)
				.setStatus(STATUS)
				.setMelding(MELDING)
				.setDistribusjonId(DISTRIBUSJONID)
				.build();
	}

	public static Varselbestilling createVarselbestilling() {
		return VarselbestillingBuilder.aVarselbestilling()
				.varselbestillingId(BESTILLINGSID)
				.varseltypeId(VARSELTYPEID)
				.utlopTidspunkt(LocalDateTime.now().plusHours(5))
				.fnr(FNR)
				.aktorId(AKTORID)
				.bestillingTidspunkt(LocalDateTime.now())
				.build();
	}

	public static Varsel createVarsel(Varselbestilling varselbestilling) {
		return VarselBuilder.aVarsel()
				.kanal(EPOST)
				.varselbestilling(varselbestilling)
				.varselId("123")
				.status(SENDT)
				.varselTekst("varseltekst")
				.erRevarsel(false)
				.build();
	}
}
