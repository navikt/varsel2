package no.nav.varsel.consumer.dokmet.to;

import lombok.Builder;
import lombok.Data;
import no.nav.varsel.domain.code.KanalCode;

import java.util.Set;

@Data
@Builder
public class Varselinfo {
	Set<KanalCode> preferertKanal;
	String varselNavn;
	String varseltypeId;
	String varselForDistKanal;
	String varselKategori;
	boolean inaktiv;
	String varselUrl;
	Integer revarslingIntervall;
	Integer antallRevarsling;
	Set<Varselmal> maler;

	public Varselmal getMal(KanalCode kanalCode) {
		return maler.stream().filter(m -> m.getKanal() == kanalCode).findFirst().get();
	}

}