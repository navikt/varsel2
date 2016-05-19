package no.nav.varsel.web.selftest.support;

/**
 * Result enum for resources, aura standard
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public enum Result {

	OK(0), WARNING(2), ERROR(1);

	public final int auraCode;

	Result(int auraCode) {
		this.auraCode = auraCode;
	}
}
