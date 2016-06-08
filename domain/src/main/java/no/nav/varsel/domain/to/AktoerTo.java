package no.nav.varsel.domain.to;

/**
 * To for Aktoer
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class AktoerTo {

	public static final String PERSONIDENT_TYPE_FNR = "FNR";

	private String ident;
	private String personIdentType;
	private MottakerType mottakerType;

	public AktoerTo() {
	}

	public static AktoerTo newAktoerId(String aktoerId) {
		return new AktoerTo(MottakerType.AKTOER, aktoerId, null);
	}

	public static AktoerTo newPersonIdent(String ident) {
		return new AktoerTo(MottakerType.PERSON, ident, PERSONIDENT_TYPE_FNR);
	}

	public static AktoerTo newPersonIdent(String ident, String personIdentType) {
		return new AktoerTo(MottakerType.PERSON, ident, personIdentType);
	}

	private AktoerTo(MottakerType mottakerType, String ident, String personIdentType) {
		this.ident = ident;
		this.personIdentType = personIdentType;
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

	public String getPersonIdentType() {
		return personIdentType;
	}

	public void setPersonIdentType(String personIdentType) {
		this.personIdentType = personIdentType;
	}

	@Override
	public String toString() {
		return "AktoerTo{" +
				"ident='" + ident + '\'' +
				", personIdentType='" + personIdentType + '\'' +
				", mottakerType=" + mottakerType +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		AktoerTo aktoerTo = (AktoerTo) o;

		if (ident != null ? !ident.equals(aktoerTo.ident) : aktoerTo.ident != null) return false;
		if (personIdentType != null ? !personIdentType.equals(aktoerTo.personIdentType) : aktoerTo.personIdentType != null)
			return false;
		return mottakerType == aktoerTo.mottakerType;

	}

	@Override
	public int hashCode() {
		int result = ident != null ? ident.hashCode() : 0;
		result = 31 * result + (personIdentType != null ? personIdentType.hashCode() : 0);
		result = 31 * result + (mottakerType != null ? mottakerType.hashCode() : 0);
		return result;
	}
}
