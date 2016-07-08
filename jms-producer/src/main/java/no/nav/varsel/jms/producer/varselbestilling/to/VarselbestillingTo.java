package no.nav.varsel.jms.producer.varselbestilling.to;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * To for Varselbestilling (TVARSEL003)
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselbestillingTo {

	private String varselbestillingId;
	private String mottakerFnr;
	private String varseltypeId;
	private Map<String, String> parameters;
	private boolean revarsel;

	public String getVarselbestillingId() {
		return varselbestillingId;
	}

	public void setVarselbestillingId(String varselbestillingId) {
		this.varselbestillingId = varselbestillingId;
	}

	public String getMottakerFnr() {
		return mottakerFnr;
	}

	public void setMottakerFnr(String mottakerFnr) {
		this.mottakerFnr = mottakerFnr;
	}

	public String getVarseltypeId() {
		return varseltypeId;
	}

	public void setVarseltypeId(String varseltypeId) {
		this.varseltypeId = varseltypeId;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public boolean isRevarsel() {
		return revarsel;
	}

	public void setRevarsel(boolean revarsel) {
		this.revarsel = revarsel;
	}

	public static final class VarselbestillingToBuilder {
		private String varselbestillingId;
		String mottakerFnr;
		private String varseltypeId;
		private boolean revarsel;
		Map<String, String> parameters = Maps.newLinkedHashMap();

		private VarselbestillingToBuilder() {
		}

		public static VarselbestillingToBuilder aVarselbestillingTo() {
			return new VarselbestillingToBuilder();
		}

		public VarselbestillingToBuilder varselbestillingId(String varselbestillingId) {
			this.varselbestillingId = varselbestillingId;
			return this;
		}

		public VarselbestillingToBuilder mottakerFnr(String mottakerFnr) {
			this.mottakerFnr = mottakerFnr;
			return this;
		}

		public VarselbestillingToBuilder varseltypeId(String varseltypeId) {
			this.varseltypeId = varseltypeId;
			return this;
		}

		public VarselbestillingToBuilder revarsel(boolean revarsel) {
			this.revarsel = revarsel;
			return this;
		}

		public VarselbestillingToBuilder parameter(String key, String value) {
			this.parameters.put(key, value);
			return this;
		}

		public VarselbestillingToBuilder parameters(Map<String, String> parameters) {
			this.parameters = parameters;
			return this;
		}

		public VarselbestillingTo build() {
			VarselbestillingTo varselbestillingTo = new VarselbestillingTo();
			varselbestillingTo.setVarselbestillingId(varselbestillingId);
			varselbestillingTo.setMottakerFnr(mottakerFnr);
			varselbestillingTo.setVarseltypeId(varseltypeId);
			varselbestillingTo.setRevarsel(revarsel);
			varselbestillingTo.setParameters(parameters);
			return varselbestillingTo;
		}
	}
}
