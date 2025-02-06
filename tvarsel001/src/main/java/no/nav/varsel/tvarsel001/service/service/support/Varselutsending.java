package no.nav.varsel.tvarsel001.service.service.support;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import no.nav.varsel.domain.code.KanalCode;

@ToString
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Varselutsending {
	private KanalCode kanal;
	private String varselUrl;
	private String varselTekst;
	private String varselTittel;
}
