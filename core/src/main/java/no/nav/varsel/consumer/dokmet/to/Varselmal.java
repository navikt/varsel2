package no.nav.varsel.consumer.dokmet.to;

import no.nav.varsel.domain.code.KanalCode;

public record Varselmal(
		KanalCode kanal,
		String tittel,
		String foerstegangsTekst,
		String revarslingTekst
) {
}