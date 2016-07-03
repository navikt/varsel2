package no.nav.varsel.service.tvarsel005.to;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lars Aune
 */
public class VarselbestillingTo {
	private List<VarselTo> varsler = new ArrayList<>();
	private String varseltypeId;
	private String fnr;
	private String aktoerId;
	private LocalDateTime bestillingstidspunkt;
	private Integer revarslingsIntervall;
	private LocalDateTime sisteVarselUtsendelse;

	public List<VarselTo> getVarsler() {
		return varsler;
	}

	private void setVarsler(List<VarselTo> varsler) {
		this.varsler = varsler;
	}

	public String getVarseltypeId() {
		return varseltypeId;
	}

	private void setVarseltypeId(String varseltypeId) {
		this.varseltypeId = varseltypeId;
	}

	public String getFnr() {
		return fnr;
	}

	private void setFnr(String fnr) {
		this.fnr = fnr;
	}

	public String getAktoerId() {
		return aktoerId;
	}

	private void setAktoerId(String aktoerId) {
		this.aktoerId = aktoerId;
	}

	public LocalDateTime getBestillingstidspunkt() {
		return bestillingstidspunkt;
	}

	private void setBestillingstidspunkt(LocalDateTime bestillingstidspunkt) {
		this.bestillingstidspunkt = bestillingstidspunkt;
	}

	public Integer getRevarslingsIntervall() {
		return revarslingsIntervall;
	}

	private void setRevarslingsIntervall(Integer revarslingsIntervall) {
		this.revarslingsIntervall = revarslingsIntervall;
	}

	public LocalDateTime getSisteVarselUtsendelse() {
		return sisteVarselUtsendelse;
	}

	private void setSisteVarselUtsendelse(LocalDateTime sisteVarselUtsendelse) {
		this.sisteVarselUtsendelse = sisteVarselUtsendelse;
	}

	public static final class Builder {
		private String varseltypeId;
		private String fnr;
		private String aktoerId;
		private LocalDateTime bestillingstidspunkt;
		private Integer revarslingIntervall;
		private LocalDateTime sisteVarselUtsendelse;
		private List<VarselTo> varsler;

		public VarselbestillingTo build() {
			VarselbestillingTo result = new VarselbestillingTo();
			result.setVarseltypeId(this.varseltypeId);
			result.setFnr(this.fnr);
			result.setAktoerId(this.aktoerId);
			result.setBestillingstidspunkt(this.bestillingstidspunkt);
			result.setRevarslingsIntervall(this.revarslingIntervall);
			result.setSisteVarselUtsendelse(this.sisteVarselUtsendelse);
			if (this.varsler != null) {
				result.varsler.addAll(this.varsler);
			}
			return result;
		}

		public Builder varseltypeId(String varseltypeId) {
			this.varseltypeId = varseltypeId;
			return this;
		}

		public Builder fnr(String fnr) {
			this.fnr = fnr;
			return this;
		}

		public Builder aktoerId(String aktoerId) {
			this.aktoerId = aktoerId;
			return this;
		}

		public Builder bestillingstidspunkt(LocalDateTime bestillingstidspunkt) {
			this.bestillingstidspunkt = bestillingstidspunkt;
			return this;
		}

		public Builder revarslingIntervall(Integer revarslingIntervall) {
			this.revarslingIntervall = revarslingIntervall;
			return this;
		}

		public Builder sisteVarselUtsendelse(LocalDateTime sisteVarselUtsendelse) {
			this.sisteVarselUtsendelse = sisteVarselUtsendelse;
			return this;
		}

		public Builder varsler(List<VarselTo> varsler) {
			this.varsler = varsler;
			return this;
		}
	}
}
