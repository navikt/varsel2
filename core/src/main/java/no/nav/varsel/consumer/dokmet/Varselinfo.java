package no.nav.varsel.consumer.dokmet;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import no.nav.varsel.domain.code.KanalCode;

import java.util.Set;

@Value
@Builder
public class Varselinfo {

	@With
	Set<KanalCode> preferertKanal;

	boolean inaktiv;
	String varselUrl;
	Set<Varselmal> maler;

	public Varselmal getMal(KanalCode kanalCode) {
		return maler.stream()
				.filter(m -> m.kanal() == kanalCode)
				.findFirst()
				.get();
	}

}