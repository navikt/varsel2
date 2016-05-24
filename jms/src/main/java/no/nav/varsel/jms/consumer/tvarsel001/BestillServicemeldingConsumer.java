package no.nav.varsel.jms.consumer.tvarsel001;


import static no.nav.varsel.jms.consumer.JmsConsumer.ConsumerNames.BESTILL_SERVICEMELDING_NAME;

import no.nav.melding.virksomhet.varsel.v1.varsel.Varsel;
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
 * Consumer for TVARSEL001 BestillServicemelding
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Component
public class BestillServicemeldingConsumer {
	private static final String BESTILL_SERVICEMELDING_QUEUE = "bestillServicemelding";
	private static final Logger LOGG = LoggerFactory.getLogger(BestillServicemeldingConsumer.class);

	@JmsListener(destination = BESTILL_SERVICEMELDING_QUEUE, id = BESTILL_SERVICEMELDING_NAME)
	public JmsReply bestillServicemelding(@Payload JAXBElement<Varsel> varselJAXBElement,
										  @Headers Map headers) {
		handleVarsel(varselJAXBElement.getValue());
		return JmsReply.reply(headers);
	}

	private void handleVarsel(Varsel varsel) {
		LOGG.info("Mottat varsel " + varsel.getVarslingstype().getValue());
	}
}
