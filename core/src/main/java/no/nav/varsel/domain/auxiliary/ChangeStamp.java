package no.nav.varsel.domain.auxiliary;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
public class ChangeStamp implements Serializable {

	private static final long serialVersionUID = 61541164562562288L;

	@Column(name = "opprettet_av", nullable = false, updatable = false)
	private String opprettetAv;

	@Column(name = "opprettet_dato", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
	private LocalDateTime opprettetDato;

	@Column(name = "endret_av")
	private String endretAv;

	@Column(name = "endret_dato", columnDefinition = "TIMESTAMP")
	private LocalDateTime endretDato;

	/**
	 * Constructs a new ChangeStamp. The constructor should only be called once, when the object embedding this
	 * <code>ChangeStamp</code> object is actually created for the first time.
	 *
	 * @param userId the user id that creates object embedding this <code>ChangeStamp</code> object
	 */
	public ChangeStamp(String userId) {
		this.opprettetAv = userId;
		opprettetDato = LocalDateTime.now();
	}

	/**
	 * No-arg constructor should only be used by persistence provider.
	 * The application should use the parameterized constructor.
	 */
	ChangeStamp() {
	}

	/**
	 * Method called whenever the object embedding this <code>ChangeStamp</code> object has been updated.
	 *
	 * @param userId user id that made the update
	 */
	public void updatedBy(String userId) {
		endretAv = userId;
		endretDato = LocalDateTime.now();
	}

	public String getOpprettetAv() {
		return opprettetAv;
	}

	public LocalDateTime getOpprettetDato() {
		return opprettetDato;
	}

	public String getEndretAv() {
		return endretAv;
	}

	public LocalDateTime getEndretDato() {
		return endretDato;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "ChangeStamp [opprettetAv=" + opprettetAv + ", opprettetDato=" + opprettetDato + ", endretAv=" + endretAv
				+ ", endretDato=" + endretDato + "]";
	}

}
