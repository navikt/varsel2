package no.nav.varsel.jms.consumer.tvarsel003;

import static no.nav.varsel.jms.consumer.JmsConsumer.BESTILL_VARSEL;
import static no.nav.varsel.jms.consumer.JmsConsumer.ConsumerNames.BESTILL_VARSEL_NAME;

import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.VarselMedHandling;
import no.nav.varsel.domain.exception.NoJmsBackoutException;
import no.nav.varsel.jms.consumer.AbstractJmsConsumer;
import no.nav.varsel.jms.consumer.tvarsel003.support.BestillVarselMapper;
import no.nav.varsel.jms.to.xml.JmsReply;
import no.nav.varsel.service.BestillVarselService;
import no.nav.varsel.service.support.exception.FunctionalVarselException;
import no.nav.varsel.service.to.BestillVarselTo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.jms.TextMessage;

/**
 * Jms Consumer for TVARSEL003 BestillVarsel
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Component
public class BestillVarselConsumer extends AbstractJmsConsumer<VarselMedHandling> {

	private static final Logger LOGG = LoggerFactory.getLogger(BestillVarselConsumer.class);

	private static final String BESTILL_VARSEL_QUEUE = "bestillVarselQueue";

	@Inject
	private BestillVarselMapper bestillVarselMapper;
	@Inject
	private BestillVarselService bestillVarselService;

	public BestillVarselConsumer() {
		super(BESTILL_VARSEL, VarselMedHandling.class);
	}

	@Override
	@JmsListener(destination = BESTILL_VARSEL_QUEUE, id = BESTILL_VARSEL_NAME)
	public JmsReply listen(TextMessage message) {
		return doListen(message);
	}

	@Override
	protected void handleMessage(VarselMedHandling message) {
		try {
			BestillVarselTo to = bestillVarselMapper.map(message);
			to.validateTvarsel003Input();
			LOGG.debug(String.format("Mottat varsel %s til %s", to.getVarslingstype(), to.craeteAktoerTo()));

			bestillVarselService.bestillVarsel(to);
		} catch (FunctionalVarselException e) {
			throw new NoJmsBackoutException(e);
		}
	}
}
