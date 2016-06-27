package no.nav.varsel.jms.consumer.tvarsel001;


import static no.nav.varsel.jms.consumer.JmsConsumer.BESTILL_SERVICEMELDING;
import static no.nav.varsel.jms.consumer.JmsConsumer.ConsumerNames.BESTILL_SERVICEMELDING_NAME;

import no.nav.melding.virksomhet.varsel.v1.varsel.Varsel;
import no.nav.varsel.jms.consumer.AbstractJmsConsumer;
import no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapper;
import no.nav.varsel.jms.to.xml.JmsReply;
import no.nav.varsel.service.ServicemeldingService;
import no.nav.varsel.service.to.BestillVarselTo;
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
public class BestillServicemeldingConsumer extends AbstractJmsConsumer<Varsel> {

	private static final Logger LOGG = LoggerFactory.getLogger(BestillServicemeldingConsumer.class);

	private static final String BESTILL_SERVICEMELDING_QUEUE = "bestillServicemeldingQueue";

	@Inject
	private BestillServicemeldingMapper bestillServicemeldingMapper;
	@Inject
	private ServicemeldingService servicemeldingService;

	public BestillServicemeldingConsumer() {
		super(BESTILL_SERVICEMELDING, Varsel.class);
	}

	@Override
	@JmsListener(destination = BESTILL_SERVICEMELDING_QUEUE, id = BESTILL_SERVICEMELDING_NAME)
	public JmsReply listen(TextMessage message) {
		return doListen(message);
	}

	@Override
	protected void handleMessage(Varsel varsel) {
		BestillVarselTo to = bestillServicemeldingMapper.map(varsel);
		to.validateTvarsel001Input();
		LOGG.debug(String.format("Mottatt varsel %s til %s", to.getVarseltypeId(), to.createAktoerTo()));

		servicemeldingService.bestillServicemelding(to);
	}
}
