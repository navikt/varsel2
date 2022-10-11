package no.nav.varsel.jms.consumer.tvarsel001;


import no.nav.melding.virksomhet.varsel.v1.varsel.Varsel;
import no.nav.varsel.domain.exception.NoJmsBackoutException;
import no.nav.varsel.jms.consumer.AbstractJmsConsumer;
import no.nav.varsel.jms.consumer.ObjectMessageWrapper;
import no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapper;
import no.nav.varsel.jms.to.xml.JmsReply;
import no.nav.varsel.service.ServicemeldingService;
import no.nav.varsel.service.to.BestillVarselTo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.TextMessage;

import static no.nav.varsel.jms.consumer.JmsConsumer.BESTILL_SERVICEMELDING;
import static no.nav.varsel.jms.consumer.JmsConsumer.ConsumerNames.BESTILL_SERVICEMELDING_NAME;
import static no.nav.varsel.util.MDCGenerate.clearCallId;
import static no.nav.varsel.util.MDCGenerate.generateCallId;

/**
 * Consumer for TVARSEL001 BestillServicemelding
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Component
public class BestillServicemeldingConsumer extends AbstractJmsConsumer<Varsel> {

	private static final Logger log = LoggerFactory.getLogger(BestillServicemeldingConsumer.class);

	private static final String BESTILL_SERVICEMELDING_QUEUE = "bestillServicemeldingQueue";
	private static final String BESTILL_SERVICEMELDING_FUNKSJONELL_FEIL_QUEUE = "bestillServicemeldingFunksjonellFeilQueue";

	private final BestillServicemeldingMapper bestillServicemeldingMapper;
	private final ServicemeldingService servicemeldingService;

	public BestillServicemeldingConsumer(BestillServicemeldingMapper bestillServicemeldingMapper, ServicemeldingService servicemeldingService, JmsTemplate funksjonellFeilSendJmsTemplate) {
		super(BESTILL_SERVICEMELDING, funksjonellFeilSendJmsTemplate, Varsel.class);
		this.bestillServicemeldingMapper = bestillServicemeldingMapper;
		this.servicemeldingService = servicemeldingService;
	}

	@Override
	@JmsListener(destination = BESTILL_SERVICEMELDING_QUEUE, id = BESTILL_SERVICEMELDING_NAME)
	public JmsReply listen(TextMessage message) {
		return doListen(message);
	}

	@Override
	protected void handleMessage(ObjectMessageWrapper<Varsel> varsel) {
		generateCallId();
		BestillVarselTo to = bestillServicemeldingMapper.map(varsel);
		log.info("bestillServicemelding mottatt med varselTypeId={}", to.getVarseltypeId());
		to.validateTvarsel001Input();
		servicemeldingService.bestillServicemelding(to);
		clearCallId();
	}

	@Override
	protected void performWriteToFunctionalErrorQueue(TextMessage message) {
		jmsSend.send(BESTILL_SERVICEMELDING_FUNKSJONELL_FEIL_QUEUE, session -> message);
	}
}
