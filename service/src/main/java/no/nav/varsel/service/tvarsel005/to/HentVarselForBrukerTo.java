package no.nav.varsel.service.tvarsel005.to;

import java.time.LocalDateTime;

public class HentVarselForBrukerTo {
	private String aktoerId;
	private String fnr;
	private LocalDateTime datoTom;
	private LocalDateTime datoFom;

	public String getAktoerId() {
		return aktoerId;
	}

	private void setAktoerId(String aktoerId) {
		this.aktoerId = aktoerId;
	}

	public String getFnr() {
		return fnr;
	}

	private void setFnr(String fnr) {
		this.fnr = fnr;
	}

	public LocalDateTime getDatoTom() {
		return datoTom;
	}

	private void setDatoTom(LocalDateTime datoTom) {
		this.datoTom = datoTom;
	}

	public LocalDateTime getDatoFom() {
		return datoFom;
	}

	private void setDatoFom(LocalDateTime datoFom) {
		this.datoFom = datoFom;
	}

	public static final class Builder {
		private String aktoerId;
		private String fnr;
		private LocalDateTime datoFom;
		private LocalDateTime datoTom;

		private Builder() {
			//Avoid public instantiation
		}
		public static Builder aHentVarselForBrukerTo() {
			return new Builder();
		}

		public Builder aktoerId(String aktoerId) {
			this.aktoerId = aktoerId;
			return this;
		}

		public Builder fnr(String fnr) {
			this.fnr = fnr;
			return this;
		}

		public Builder datoFom(LocalDateTime datoFom) {
			this.datoFom = datoFom;
			return this;
		}

		public Builder datoTom(LocalDateTime datoTom) {
			this.datoTom = datoTom;
			return this;
		}

		public HentVarselForBrukerTo build() {
			HentVarselForBrukerTo result = new HentVarselForBrukerTo();
			result.setAktoerId(aktoerId);
			result.setFnr(fnr);
			result.setDatoFom(datoFom);
			result.setDatoTom(datoTom);
			return result;
		}
	}
}
