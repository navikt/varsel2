package no.nav.varsel.jms.consumer.tvarsel004;

import static no.nav.varsel.jms.consumer.JmsConsumer.ConsumerNames.REVARSEL_STOPP_NAME;
import static no.nav.varsel.jms.consumer.JmsConsumer.REVARSEL_STOPP;

import no.nav.melding.virksomhet.stopprevarsel.v1.stopprevarsel.StoppReVarsel;
import no.nav.varsel.jms.consumer.AbstractJmsConsumer;
import no.nav.varsel.jms.consumer.ObjectMessageWrapper;
import no.nav.varsel.jms.consumer.tvarsel004.support.StoppReVarselMapper;
import no.nav.varsel.jms.to.xml.JmsReply;
import no.nav.varsel.service.StoppReVarselService;
import no.nav.varsel.service.tvarsel004.to.StoppReVarselTo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.jms.TextMessage;

/**
 * Consumer for TVARSEL004 StoppRevarsel
 *
 * @author Hiep Luong Nguyen, Computas
 */
@Component
public class StoppReVarselConsumer extends AbstractJmsConsumer<StoppReVarsel> {
	private static final Logger LOGG = LoggerFactory.getLogger(StoppReVarselConsumer.class);
	private static final String REVARSEL_STOPP_QUEUE = "revarselStoppQueue";

	@Inject
	private StoppReVarselMapper stoppReVarselMapper;
	@Inject
	private StoppReVarselService stoppReVarselService;

	public StoppReVarselConsumer() {
		super(REVARSEL_STOPP, StoppReVarsel.class);
	}

	@Override
	@JmsListener(destination = REVARSEL_STOPP_QUEUE, id = REVARSEL_STOPP_NAME)
	public JmsReply listen(TextMessage message) {
		return doListen(message);
	}

	@Override
	protected void handleMessage(ObjectMessageWrapper<StoppReVarsel> stoppReVarselWithMessage) {
		StoppReVarsel stoppRevarsel = stoppReVarselWithMessage.getObject();
		LOGG.info("Mottat kall for å stoppe revarsel for varselbestillingId=" + stoppRevarsel.getVarselbestillingId());
		StoppReVarselTo stoppReVarselTo = stoppReVarselMapper.map(stoppRevarsel);
		stoppReVarselTo.validateTo();
		stoppReVarselService.behandleVarselbestilling(stoppReVarselTo);
	}
}
