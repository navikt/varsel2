package no.nav.varsel.tvarsel001.jms.consumer;

import no.nav.melding.virksomhet.varsel.v1.varsel.XMLVarsel;
import no.nav.varsel.tvarsel001.jms.xml.JmsReply;
import no.nav.varsel.tvarsel001.service.service.ServicemeldingService;
import no.nav.varsel.tvarsel001.service.service.to.BestillVarselTo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import jakarta.jms.TextMessage;

import static no.nav.varsel.tvarsel001.jms.consumer.JmsConsumer.BESTILL_SERVICEMELDING;
import static no.nav.varsel.tvarsel001.jms.consumer.JmsConsumer.ConsumerNames.BESTILL_SERVICEMELDING_NAME;
import static no.nav.varsel.util.MDCGenerate.clearCallId;
import static no.nav.varsel.util.MDCGenerate.generateCallId;

@Component
public class BestillServicemeldingConsumer extends AbstractJmsConsumer<XMLVarsel> {

	private static final Logger log = LoggerFactory.getLogger(BestillServicemeldingConsumer.class);

	private static final String BESTILL_SERVICEMELDING_QUEUE = "bestillServicemeldingQueue";
	private static final String BESTILL_SERVICEMELDING_FUNKSJONELL_FEIL_QUEUE = "bestillServicemeldingFunksjonellFeilQueue";

	private final BestillServicemeldingMapper bestillServicemeldingMapper;
	private final ServicemeldingService servicemeldingService;

	public BestillServicemeldingConsumer(BestillServicemeldingMapper bestillServicemeldingMapper, ServicemeldingService servicemeldingService, JmsTemplate funksjonellFeilSendJmsTemplate) {
		super(BESTILL_SERVICEMELDING, funksjonellFeilSendJmsTemplate, XMLVarsel.class);
		this.bestillServicemeldingMapper = bestillServicemeldingMapper;
		this.servicemeldingService = servicemeldingService;
	}

	@Override
	@JmsListener(destination = BESTILL_SERVICEMELDING_QUEUE, id = BESTILL_SERVICEMELDING_NAME)
	public JmsReply listen(TextMessage message) {
		return doListen(message);
	}

	@Override
	protected void handleMessage(ObjectMessageWrapper<XMLVarsel> varsel) {
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
