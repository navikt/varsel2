package no.nav.varsel.service.tvarsel005.to;

import java.util.ArrayList;
import java.util.List;

/**
 * Transferobject for HentVarselForBrukerResponse
 * @author Lars Aune
 */
public class HentVarselForBrukerResponseTo {
	private List<VarselbestillingTo> brukersVarsler = new ArrayList<>();

	public List<VarselbestillingTo> getBrukersVarsler() {
		return brukersVarsler;
	}

	public static final class Builder {
		private List<VarselbestillingTo> brukersVarsler;

		public HentVarselForBrukerResponseTo build() {
			HentVarselForBrukerResponseTo result = new HentVarselForBrukerResponseTo();
			if (brukersVarsler != null) {
				result.brukersVarsler.addAll(brukersVarsler);
			}
			return result;
		}

		public Builder brukersVarsler(List<VarselbestillingTo> brukersVarsler) {
			this.brukersVarsler = brukersVarsler;
			return this;
		}
	}
}
