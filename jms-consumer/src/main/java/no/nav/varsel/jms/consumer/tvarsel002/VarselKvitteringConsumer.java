package no.nav.varsel.jms.consumer.tvarsel002;

import static no.nav.varsel.jms.consumer.JmsConsumer.ConsumerNames.VARSEL_KVITTERING_NAME;

import no.nav.melding.virksomhet.varselkvittering.v1.varselkvittering.VarselKvittering;
import no.nav.varsel.domain.Constants;
import no.nav.varsel.jms.consumer.AbstractJmsConsumer;
import no.nav.varsel.jms.to.xml.JmsReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.TextMessage;

/**
 * Consumer for TVARSEL002 VarselKvittering
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Component
public class VarselKvitteringConsumer extends AbstractJmsConsumer {

	private static final Logger LOGG = LoggerFactory.getLogger(VarselKvitteringConsumer.class);
	private static final String VARSEL_KVITTERING_QUEUE = "varselKvittering";

	@JmsListener(destination = VARSEL_KVITTERING_QUEUE, id = VARSEL_KVITTERING_NAME)
	public JmsReply varselKvittering(TextMessage message) {
		MDC.put(Constants.USER_ID, "TVARSEL002");
		handleMessage(unmarshal(message, VarselKvittering.class));
		return reply(message);
	}

	private void handleMessage(VarselKvittering kvittering) {
		LOGG.info("Mottat varsel " + kvittering.getStatus());
	}
}
