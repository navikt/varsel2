package no.nav.varsel.domain.object.worktable;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Arbeidstabell for BVARSEL001
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Entity
@Table(name = "ARBTB_BVARSEL001")
public class Bvarsel001WorkTable implements Serializable {

	private static final long serialVersionUID = 791168843L;

	@Id
	@Column(name = "varselbestilling_id", nullable = false, updatable = false)
	private String varselbestillingId;

	@Enumerated(EnumType.STRING)
	@ColumnDefault("'OPPRETTET'")
	@Column(name = "arbeid_status", nullable = false)
	private ArbeidStatus arbeidStatus = ArbeidStatus.OPPRETTET;

	public Bvarsel001WorkTable() {
	}

	public Bvarsel001WorkTable(String varselbestillingId, ArbeidStatus arbeidStatus) {
		this.varselbestillingId = varselbestillingId;
		this.arbeidStatus = arbeidStatus;
	}

	public String getVarselbestillingId() {
		return varselbestillingId;
	}

	public void setVarselbestillingId(String varselbestillingId) {
		this.varselbestillingId = varselbestillingId;
	}

	public ArbeidStatus getArbeidStatus() {
		return arbeidStatus;
	}

	public void setArbeidStatus(ArbeidStatus arbeidStatus) {
		this.arbeidStatus = arbeidStatus;
	}
}
