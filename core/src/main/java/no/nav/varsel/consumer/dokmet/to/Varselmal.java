package no.nav.varsel.consumer.dokmet.to;

import lombok.Builder;
import lombok.Data;
import no.nav.varsel.domain.code.KanalCode;

@Data
@Builder
public class Varselmal {
	KanalCode kanal;
	String tittel;
	String foerstegangsTekst;
	String revarslingTekst;
}