package no.nav.varsel.consumer.dokmet.to;

import com.google.common.collect.Sets;
import lombok.ToString;
import no.nav.varsel.domain.code.KanalCode;

import java.util.Set;

@ToString
public class Varselinfo {
	private Set<KanalCode> preferertKanal = Sets.newHashSet();
	private String varselNavn;
	private String varseltypeId;
	private String varselForDistKanal;
	private String varselKategori;
	private boolean inaktiv;
	private String varselUrl;
	private Integer revarslingIntervall;
	private Integer antallRevarsling;
	private Set<Varselmal> maler;

	public void addPreferertKanal(KanalCode kanal) {
		this.preferertKanal.add(kanal);
	}

	public Varselmal getMal(KanalCode kanalCode) {
		return maler.stream().filter(m -> m.getKanal() == kanalCode).findFirst().get();
	}

	public Set<KanalCode> getPreferertKanal() {
		return preferertKanal;
	}

	public void setPreferertKanal(Set<KanalCode> preferertKanal) {
		this.preferertKanal = preferertKanal;
	}

	public String getVarselNavn() {
		return varselNavn;
	}

	public void setVarselNavn(String varselNavn) {
		this.varselNavn = varselNavn;
	}

	public String getVarseltypeId() {
		return varseltypeId;
	}

	public void setVarseltypeId(String varseltypeId) {
		this.varseltypeId = varseltypeId;
	}

	public String getVarselForDistKanal() {
		return varselForDistKanal;
	}

	public void setVarselForDistKanal(String varselForDistKanal) {
		this.varselForDistKanal = varselForDistKanal;
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

	public String getVarselUrl() {
		return varselUrl;
	}

	public void setVarselUrl(String varselUrl) {
		this.varselUrl = varselUrl;
	}

	public Integer getRevarslingIntervall() {
		return revarslingIntervall;
	}

	public void setRevarslingIntervall(Integer revarslingIntervall) {
		this.revarslingIntervall = revarslingIntervall;
	}

	public Integer getAntallRevarsling() {
		return antallRevarsling;
	}

	public void setAntallRevarsling(Integer antallRevarsling) {
		this.antallRevarsling = antallRevarsling;
	}

	public Set<Varselmal> getMaler() {
		return maler;
	}

	public void setMaler(Set<Varselmal> maler) {
		this.maler = maler;
	}

	public static final class VarselInfoToBuilder {
		private Set<KanalCode> preferertKanal = Sets.newHashSet();
		private String varseltypeId;
		private String varselNavn;
		private String varselForDistKanal;
		private String varselKategori;
		private boolean inaktiv;
		private String varselUrl;
		private Integer revarslingIntervall;
		private Integer antallRevarsling;
		private Set<Varselmal> maler;

		private VarselInfoToBuilder() {
		}

		public static VarselInfoToBuilder aVarselInfoTo() {
			return new VarselInfoToBuilder();
		}

		public VarselInfoToBuilder preferertKanal(Set<KanalCode> preferertKanal) {
			this.preferertKanal = preferertKanal;
			return this;
		}

		public VarselInfoToBuilder varseltypeId(String varseltypeId) {
			this.varseltypeId = varseltypeId;
			return this;
		}

		public VarselInfoToBuilder varselNavn(String varselNavn) {
			this.varselNavn = varselNavn;
			return this;
		}

		public VarselInfoToBuilder varselForDistKanal(String varselForDistKanal) {
			this.varselForDistKanal = varselForDistKanal;
			return this;
		}

		public VarselInfoToBuilder varselKategori(String varselKategori) {
			this.varselKategori = varselKategori;
			return this;
		}

		public VarselInfoToBuilder inaktiv(boolean inaktiv) {
			this.inaktiv = inaktiv;
			return this;
		}

		public VarselInfoToBuilder varselUrl(String varselUrl) {
			this.varselUrl = varselUrl;
			return this;
		}

		public VarselInfoToBuilder revarslingIntervall(Integer revarslingIntervall) {
			this.revarslingIntervall = revarslingIntervall;
			return this;
		}

		public VarselInfoToBuilder antallRevarsling(Integer antallRevarsling) {
			this.antallRevarsling = antallRevarsling;
			return this;
		}

		public VarselInfoToBuilder maler(Set<Varselmal> maler) {
			this.maler = maler;
			return this;
		}

		public Varselinfo build() {
			Varselinfo varselinfo = new Varselinfo();
			varselinfo.setPreferertKanal(preferertKanal);
			varselinfo.setVarseltypeId(varseltypeId);
			varselinfo.setVarselNavn(varselNavn);
			varselinfo.setVarselForDistKanal(varselForDistKanal);
			varselinfo.setVarselKategori(varselKategori);
			varselinfo.setInaktiv(inaktiv);
			varselinfo.setVarselUrl(varselUrl);
			varselinfo.setRevarslingIntervall(revarslingIntervall);
			varselinfo.setAntallRevarsling(antallRevarsling);
			varselinfo.setMaler(maler);
			return varselinfo;
		}
	}
}
