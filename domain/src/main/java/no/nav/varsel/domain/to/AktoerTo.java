package no.nav.varsel.domain.to;

/**
 * To for Aktoer
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class AktoerTo {

	private String ident;
	private MottakerType mottakerType;

	public AktoerTo() {
	}

	public AktoerTo(String ident, MottakerType mottakerType) {
		this.ident = ident;
		this.mottakerType = mottakerType;
	}

	public String getIdent() {
		return ident;
	}

	public void setIdent(String ident) {
		this.ident = ident;
	}

	public MottakerType getMottakerType() {
		return mottakerType;
	}

	public void setMottakerType(MottakerType mottakerType) {
		this.mottakerType = mottakerType;
	}

	@Override
	public String toString() {
		return "AktoerTo{" +
				"ident='" + ident + '\'' +
				", mottakerType=" + mottakerType +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		AktoerTo aktoerTo = (AktoerTo) o;

		if (ident != null ? !ident.equals(aktoerTo.ident) : aktoerTo.ident != null) return false;
		return mottakerType == aktoerTo.mottakerType;
	}

	@Override
	public int hashCode() {
		int result = ident != null ? ident.hashCode() : 0;
		result = 31 * result + (mottakerType != null ? mottakerType.hashCode() : 0);
		return result;
	}
}
