package no.nav.varsel.jms.consumer.tvarsel002;

import static no.nav.varsel.jms.consumer.JmsConsumer.ConsumerNames.VARSEL_KVITTERING_NAME;
import static no.nav.varsel.jms.consumer.JmsConsumer.VARSEL_KVITTERING;

import no.nav.melding.virksomhet.varselkvittering.v1.varselkvittering.VarselKvittering;
import no.nav.varsel.domain.exception.NoJmsBackoutException;
import no.nav.varsel.jms.consumer.AbstractJmsConsumer;
import no.nav.varsel.jms.consumer.tvarsel002.support.MottaVarselKvitteringMapper;
import no.nav.varsel.jms.to.xml.JmsReply;
import no.nav.varsel.service.MottaVarselKvitteringService;
import no.nav.varsel.service.support.exception.FunctionalVarselException;
import no.nav.varsel.service.tvarsel002.to.MottaVarselKvitteringTo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
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

	static final String TVARSEL002 = "tvarsel002";

	@Inject
	private MottaVarselKvitteringMapper mottaVarselKvitteringMapper;
	@Inject
	private MottaVarselKvitteringService mottaVarselKvitteringService;

	public VarselKvitteringConsumer() {
		super(VARSEL_KVITTERING, VarselKvittering.class);
	}

	@Override
	@JmsListener(destination = VARSEL_KVITTERING_QUEUE, id = VARSEL_KVITTERING_NAME)
	public JmsReply listen(TextMessage message) {
		return doListen(message);
	}

	@Override
	protected void handleMessage(VarselKvittering kvittering) {
		LOGG.debug("Mottatt kvittering " + kvittering.getStatus() + " , varselId=" + kvittering.getVarselId());
		try {
			MottaVarselKvitteringTo to = mottaVarselKvitteringMapper.map(kvittering);
			to.validateTo();
			mottaVarselKvitteringService.behandleKvitteringsmelding(to);
		} catch (IllegalArgumentException | FunctionalVarselException e) {
			throw new NoJmsBackoutException(e);
		}
	}
}
