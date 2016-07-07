package no.nav.varsel.service.tvarsel005.to;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Transferobject for HentVarselForBrukerResponse
 *
 * @author Lars Aune
 */
public class HentVarselForBrukerResponseTo {
	private List<VarselbestillingTo> varselbestillingTos = new ArrayList<>();

	public List<VarselbestillingTo> getVarselbestillingTos() {
		return varselbestillingTos;
	}

	public void setVarselbestillingTos(List<VarselbestillingTo> varselbestillingTos) {
		this.varselbestillingTos = varselbestillingTos;
	}

	public static final class Builder {
		private List<VarselbestillingTo> varselbestillingTos = new ArrayList<>();

		private Builder() {
		}

		public static Builder aHentVarselForBrukerResponseTo() {
			return new Builder();
		}

		public Builder varselbestillingTos(VarselbestillingTo... varselbestillingTos) {
			this.varselbestillingTos.clear();
			Collections.addAll(this.varselbestillingTos, varselbestillingTos);
			return this;
		}

		public Builder varselbestillingTos(Collection<VarselbestillingTo> varselbestillingTos) {
			this.varselbestillingTos.clear();
			this.varselbestillingTos.addAll(varselbestillingTos);
			return this;
		}

		public HentVarselForBrukerResponseTo build() {
			HentVarselForBrukerResponseTo hentVarselForBrukerResponseTo = new HentVarselForBrukerResponseTo();
			hentVarselForBrukerResponseTo.setVarselbestillingTos(varselbestillingTos);
			return hentVarselForBrukerResponseTo;
		}
	}
}
