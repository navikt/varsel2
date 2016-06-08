package no.nav.varsel.wsconsumer.dokkat.to;

import com.google.common.collect.Sets;
import no.nav.varsel.domain.code.KanalCode;

import java.util.Set;

/**
 * To For VarselInfo
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselInfoTo {
	private Set<KanalCode> preferertKanal = Sets.newHashSet();
	private String varselURL;
	private String varslingstype;
	private String varselForDistrKanal;
	private String varselKategori;
	private boolean inaktiv;
	private int revarslingIntervall;
	private int antallRevarsling;
	private Set<VarselMalTo> maler;

	public void addPreferertKanal(KanalCode kanal) {
		this.preferertKanal.add(kanal);
	}

	public VarselMalTo getMal(KanalCode kanalCode) {
		return maler.stream().filter(m -> m.getKanal() == kanalCode).findFirst().get();
	}

	public Set<KanalCode> getPreferertKanal() {
		return preferertKanal;
	}

	public void setPreferertKanal(Set<KanalCode> preferertKanal) {
		this.preferertKanal = preferertKanal;
	}

	public String getVarselURL() {
		return varselURL;
	}

	public void setVarselURL(String varselURL) {
		this.varselURL = varselURL;
	}

	public String getVarslingstype() {
		return varslingstype;
	}

	public void setVarslingstype(String varslingstype) {
		this.varslingstype = varslingstype;
	}

	public String getVarselForDistrKanal() {
		return varselForDistrKanal;
	}

	public void setVarselForDistrKanal(String varselForDistrKanal) {
		this.varselForDistrKanal = varselForDistrKanal;
	}

	public String getVarselKategori() {
		return varselKategori;
	}

	public void setVarselKategori(String varselKategori) {
		this.varselKategori = varselKategori;
	}

	public boolean isInaktiv() {
		return inaktiv;
	}

	public void setInaktiv(boolean inaktiv) {
		this.inaktiv = inaktiv;
	}

	public int getRevarslingIntervall() {
		return revarslingIntervall;
	}

	public void setRevarslingIntervall(int revarslingIntervall) {
		this.revarslingIntervall = revarslingIntervall;
	}

	public int getAntallRevarsling() {
		return antallRevarsling;
	}

	public void setAntallRevarsling(int antallRevarsling) {
		this.antallRevarsling = antallRevarsling;
	}

	public Set<VarselMalTo> getMaler() {
		return maler;
	}

	public void setMaler(Set<VarselMalTo> maler) {
		this.maler = maler;
	}
}
