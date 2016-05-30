package no.nav.varsel.wsconsumer.dokkat;

import com.google.common.collect.Sets;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo;
import no.nav.varsel.wsconsumer.dokkat.to.VarselMalTo;

/**
 * VarselInfo Stub
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselInfoConsumer {

	public VarselInfoTo hentVarselInfo(String varseltype) {
		if (varseltype.equals("feil")) {
			throw new RuntimeException("boo");
		}
		// TODO PK-31739
		VarselInfoTo varselInfoTo = new VarselInfoTo();
		varselInfoTo.setPreferertKanal("DITTNAV");
		varselInfoTo.setVarselURL("URL");
		VarselMalTo varselMalTo = new VarselMalTo();
		varselMalTo.setKanal(KanalCode.DITTNAV);
		varselMalTo.setTittel("Varsel Tittel");
		varselMalTo.setFoerstegangsTekst("Første gang tekst til :mottaker");
		varselMalTo.setRevarslingTekst("Revarsling tekst til :mottaker");
		varselInfoTo.setMaler(Sets.newHashSet(varselMalTo));
		return varselInfoTo;
	}
}
