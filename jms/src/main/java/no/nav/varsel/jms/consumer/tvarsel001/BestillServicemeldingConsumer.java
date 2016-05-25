package no.nav.varsel.jms.consumer.tvarsel001;


import static no.nav.varsel.jms.consumer.JmsConsumer.ConsumerNames.BESTILL_SERVICEMELDING_NAME;

import no.nav.melding.virksomhet.varsel.v1.varsel.Varsel;
import no.nav.varsel.jms.consumer.AbstractJmsConsumer;
import no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapper;
import no.nav.varsel.jms.consumer.tvarsel001.to.BestillServicemeldingTo;
import no.nav.varsel.jms.to.xml.JmsReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.jms.TextMessage;

/**
 * Consumer for TVARSEL001 BestillServicemelding
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Component
public class BestillServicemeldingConsumer extends AbstractJmsConsumer {
	private static final String BESTILL_SERVICEMELDING_QUEUE = "bestillServicemelding";
	private static final Logger LOGG = LoggerFactory.getLogger(BestillServicemeldingConsumer.class);

	@Inject
	private BestillServicemeldingMapper bestillServicemeldingMapper;

	@JmsListener(destination = BESTILL_SERVICEMELDING_QUEUE, id = BESTILL_SERVICEMELDING_NAME)
	public JmsReply bestillServicemelding(TextMessage message) {
		handleVarsel(unmarshal(message, Varsel.class));
		return reply(message);
	}

	private void handleVarsel(Varsel varsel) {
		LOGG.info("Mottat varsel " + varsel.getVarslingstype().getValue());
		BestillServicemeldingTo to = bestillServicemeldingMapper.map(varsel);
		to.validate();
	}
}
