package no.nav.varsel.web.selftest.support;

/**
 * Result enum for resources, aura standard
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public enum Result {

	OK(0, "success"), WARNING(2, "warning"), ERROR(1, "danger");

	public final int auraCode;
	private String cssClass;

	Result(int auraCode, String cssClass) {
		this.auraCode = auraCode;
		this.cssClass = cssClass;
	}

	public String getCssClass() {
		return cssClass;
	}
}
