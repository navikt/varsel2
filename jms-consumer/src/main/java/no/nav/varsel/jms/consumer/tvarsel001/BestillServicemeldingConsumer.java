package no.nav.varsel.jms.consumer.tvarsel001;


import no.nav.melding.virksomhet.varsel.v1.varsel.Varsel;
import no.nav.varsel.jms.consumer.AbstractJmsConsumer;
import no.nav.varsel.jms.consumer.ObjectMessageWrapper;
import no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapper;
import no.nav.varsel.jms.to.xml.JmsReply;
import no.nav.varsel.service.ServicemeldingService;
import no.nav.varsel.service.to.BestillVarselTo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Autowired;
import javax.jms.TextMessage;

import static no.nav.varsel.jms.consumer.JmsConsumer.BESTILL_SERVICEMELDING;
import static no.nav.varsel.jms.consumer.JmsConsumer.ConsumerNames.BESTILL_SERVICEMELDING_NAME;

/**
 * Consumer for TVARSEL001 BestillServicemelding
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Component
public class BestillServicemeldingConsumer extends AbstractJmsConsumer<Varsel> {

	private static final Logger log = LoggerFactory.getLogger(BestillServicemeldingConsumer.class);

	private static final String BESTILL_SERVICEMELDING_QUEUE = "bestillServicemeldingQueue";

	@Autowired
	private BestillServicemeldingMapper bestillServicemeldingMapper;
	@Autowired
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
	protected void handleMessage(ObjectMessageWrapper<Varsel> varsel) {
		BestillVarselTo to = bestillServicemeldingMapper.map(varsel);
		log.info("bestillServicemelding mottatt varselBestillingId={}, varselTypeId={}", to.getVarselBestillingId(), to.getVarseltypeId());
		to.validateTvarsel001Input();
		servicemeldingService.bestillServicemelding(to);
	}
}
