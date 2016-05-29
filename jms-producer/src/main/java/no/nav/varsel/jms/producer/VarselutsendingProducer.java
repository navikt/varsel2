package no.nav.varsel.jms.producer;


import no.nav.varsel.jms.producer.varselutsending.to.VarselutsendingTo;

/**
 * Varselutsending Stub
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselutsendingProducer {

	public void produce(VarselutsendingTo varselutsendingTo) {
		if ("feilMqUt".equals(varselutsendingTo.getVarslingstype())) {
			throw new RuntimeException("mq ut feil");
		}
	}
}
