package no.nav.varsel.jms.consumer.tvarsel002;

import static no.nav.varsel.jms.consumer.JmsConsumer.ConsumerNames.VARSEL_KVITTERING_NAME;

import no.nav.melding.virksomhet.varselkvittering.v1.varselkvittering.VarselKvittering;
import no.nav.varsel.jms.consumer.AbstractJmsConsumer;
import no.nav.varsel.jms.to.xml.JmsReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.TextMessage;

/**
 * Consumer for TVARSEL002 VarselKvittering
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Component
public class VarselKvitteringConsumer extends AbstractJmsConsumer<VarselKvittering> {

	private static final Logger LOGG = LoggerFactory.getLogger(VarselKvitteringConsumer.class);

	private static final String VARSEL_KVITTERING_QUEUE = "varselKvitteringQueue";
	private static final String TVARSEL002 = "tvarsel002";

	@Override
	@JmsListener(destination = VARSEL_KVITTERING_QUEUE, id = VARSEL_KVITTERING_NAME)
	public JmsReply listen(TextMessage message) {
		return doListen(message);
	}

	@Override
	protected void handleMessage(VarselKvittering kvittering) {
		LOGG.info("Mottat varsel " + kvittering.getStatus());
	}

	@Override
	protected Class<VarselKvittering> getClazz() {
		return VarselKvittering.class;
	}

	@Override
	protected String getServiceName() {
		return TVARSEL002;
	}
}
