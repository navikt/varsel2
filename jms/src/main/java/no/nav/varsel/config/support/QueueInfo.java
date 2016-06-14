package no.nav.varsel.config.support;

import static no.nav.varsel.config.support.QueueInfo.Direction.IN;
import static no.nav.varsel.config.support.QueueInfo.Direction.OUT;

/**
 * Information about queues
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public enum QueueInfo {
	BESTILL_SERVICEMELDING("VARSELPRODUKSJON.VARSLINGER", "bestillServicemelding", IN),
	VARSEL_KVITTERING("VARSELPRODUKSJON.KVITTERING", "varselKvittering", IN),
	VARSELUTSENDING("VARSEL_INN", "varselutsending", OUT),
	BESTILL_VARSEL("VARSELPRODUKSJON.BEST_VARSEL_M_HANDLING", "bestillVarsel", IN);

	private final String fasitName;
	private final String internalName;
	private final Direction direction;

	QueueInfo(String fasitName, String internalName, Direction direction) {
		this.fasitName = fasitName;
		this.internalName = internalName;
		this.direction = direction;
	}

	public String getFasitName() {
		return fasitName;
	}

	public String getInternalName() {
		return internalName;
	}

	public Direction getDirection() {
		return direction;
	}

	public String getDescription() {
		return String.format("direction=%s fasitAlias=%s", getDirection(), getFasitName());
	}

	public enum Direction {
		IN, OUT
	}
}
