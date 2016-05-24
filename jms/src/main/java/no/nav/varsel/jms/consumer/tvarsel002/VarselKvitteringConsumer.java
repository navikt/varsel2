package no.nav.varsel.jms.consumer.tvarsel002;

import static no.nav.varsel.jms.consumer.JmsConsumer.ConsumerNames.VARSEL_KVITTERING_NAME;

import no.nav.melding.virksomhet.varselkvittering.v1.varselkvittering.VarselKvittering;
import no.nav.varsel.jms.to.JmsReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBElement;
import java.util.Map;

/**
 * Consumer for TVARSEL002 VarselKvittering
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Component
public class VarselKvitteringConsumer {

	private static final Logger LOGG = LoggerFactory.getLogger(VarselKvitteringConsumer.class);
	private static final String VARSEL_KVITTERING_QUEUE = "varselKvittering";

	@JmsListener(destination = VARSEL_KVITTERING_QUEUE, id = VARSEL_KVITTERING_NAME)
	public JmsReply varselKvittering(@Payload JAXBElement<VarselKvittering> varselKvitteringJAXBElement,
									 @Headers Map headers) {
		handleMessage(varselKvitteringJAXBElement.getValue());
		return JmsReply.reply(headers);
	}

	private void handleMessage(VarselKvittering kvittering) {
		LOGG.info("Mottat varsel " + kvittering.getStatus());
	}
}
