package no.nav.varsel.service.support;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.to.AktoerTo;

import java.time.LocalDateTime;

@ToString
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VarselutsendingTo {
	private LocalDateTime utloepstidspunkt;
	private String varseltypeId;
	private KanalCode kanal;
	private AktoerTo mottaker;
	private String varselId;
	private String varselUrl;
	private String varselTekst;
	private String varselTittel;
	private String kontaktInformasjon;
}
