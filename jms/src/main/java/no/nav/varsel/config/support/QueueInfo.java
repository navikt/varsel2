package no.nav.varsel.config.support;

import static no.nav.varsel.config.support.QueueInfo.Direction.IN;
import static no.nav.varsel.config.support.QueueInfo.Direction.OUT;

/**
 * Information about queues
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public enum QueueInfo {
	BESTILL_SERVICEMELDING("VARSELPRODUKSJON.VARSLINGER", "bestillServicemelding", IN, false),
	VARSEL_KVITTERING("VARSELPRODUKSJON.KVITTERING", "varselKvittering", IN, false),
	VARSELUTSENDING("VARSEL_INN", "varselutsending", OUT, false),
	BESTILL_VARSEL("VARSELPRODUKSJON.BEST_VARSEL_M_HANDLING", "bestillVarsel", IN, false),
	REVARSEL_STOPP("VARSELPRODUKSJON.STOPP_VARSEL_UTSENDING", "revarselStopp", IN, false),
	BESTILL_SERVICEMELDING_KONTAKTINFO("VARSELPRODUKSJON.BEST_SRVMLD_M_KONTAKT", "bestillServicemeldingMedKontaktInfo", IN, false);

	private final String fasitName;
	private final String internalName;
	private final Direction direction;
	private boolean remote;

	QueueInfo(String fasitName, String internalName, Direction direction, boolean remote) {
		this.fasitName = fasitName;
		this.internalName = internalName;
		this.direction = direction;
		this.remote = remote;
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

	public boolean isRemote() {
		return remote;
	}

	public enum Direction {
		IN, OUT
	}
}
