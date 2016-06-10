package no.nav.varsel.wsconsumer.dokkat;

import com.google.common.collect.Sets;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo;
import no.nav.varsel.wsconsumer.dokkat.to.VarselMalTo;

/**
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselInfoConsumerTest {

	public static final String VARSEL_URL = "URL";
	public static final String VARSEL_TITTEL = "Varsel Tittel";
	public static final String FOERSTE_GANG_TEKST = "Første gang tekst til {mottaker}";
	public static final String REVARSLING_TEKST = "Revarsling tekst til {mottaker}";
	public static final String VARSEL_FOR_DISTR_KANAL = "vardistkanal";
	public static final String VARSEL_KATEGORI = "varkat";
	public static final boolean INAKTIV = false;
	public static final int REVARSLING_INTERVALL = 4;
	public static final int ANTALL_REVARSLING = 2;
	public static final KanalCode PREFERERT_KANAL = KanalCode.EPOST;

	// Move to test class when implemented
	public static VarselInfoTo createVarselInfoTo(String varseltype) {
		VarselInfoTo varselInfoTo = new VarselInfoTo();
		varselInfoTo.setVarslingstype(varseltype);
		varselInfoTo.setVarselForDistrKanal(VARSEL_FOR_DISTR_KANAL);
		varselInfoTo.setVarselKategori(VARSEL_KATEGORI);
		varselInfoTo.setInaktiv(INAKTIV);
		varselInfoTo.setRevarslingIntervall(REVARSLING_INTERVALL);
		varselInfoTo.setAntallRevarsling(ANTALL_REVARSLING);
		varselInfoTo.addPreferertKanal(PREFERERT_KANAL);
		varselInfoTo.setVarselURL(VARSEL_URL);

		VarselMalTo varselMalTo = new VarselMalTo();
		varselMalTo.setKanal(KanalCode.EPOST);
		varselMalTo.setTittel(VARSEL_TITTEL);
		varselMalTo.setFoerstegangsTekst(FOERSTE_GANG_TEKST);
		varselMalTo.setRevarslingTekst(REVARSLING_TEKST);

		varselInfoTo.setMaler(Sets.newHashSet(varselMalTo));
		return varselInfoTo;
	}

}