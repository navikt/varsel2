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

	public static final String PREFERERT_KANAL = "DITTNAV";
	public static final String VARSEL_URL = "URL";
	public static final String VARSEL_TITTEL = "Varsel Tittel";
	public static final String FØRSTE_GANG_TEKST_TIL_MOTTAKER = "Første gang tekst til :mottaker";

	public VarselInfoTo hentVarselInfo(String varseltype) {
		// TODO PK-31739
		VarselInfoTo varselInfoTo = new VarselInfoTo();
		varselInfoTo.setPreferertKanal(PREFERERT_KANAL);
		varselInfoTo.setVarselURL(VARSEL_URL);
		VarselMalTo varselMalTo = new VarselMalTo();
		varselMalTo.setKanal(KanalCode.DITTNAV);
		varselMalTo.setTittel(VARSEL_TITTEL);
		varselMalTo.setFoerstegangsTekst(FØRSTE_GANG_TEKST_TIL_MOTTAKER);
		varselMalTo.setRevarslingTekst("Revarsling tekst til :mottaker");
		varselInfoTo.setMaler(Sets.newHashSet(varselMalTo));
		return varselInfoTo;
	}
}
