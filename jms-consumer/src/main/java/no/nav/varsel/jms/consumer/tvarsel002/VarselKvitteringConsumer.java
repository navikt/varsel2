package no.nav.varsel.jms.consumer.tvarsel002;

import no.nav.melding.virksomhet.varselkvittering.v1.varselkvittering.VarselKvittering;
import no.nav.varsel.domain.exception.NoJmsBackoutException;
import no.nav.varsel.jms.consumer.AbstractJmsConsumer;
import no.nav.varsel.jms.consumer.ObjectMessageWrapper;
import no.nav.varsel.jms.consumer.tvarsel002.support.MottaVarselKvitteringMapper;
import no.nav.varsel.jms.to.xml.JmsReply;
import no.nav.varsel.service.MottaVarselKvitteringService;
import no.nav.varsel.service.tvarsel002.to.MottaVarselKvitteringTo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Autowired;
import javax.jms.TextMessage;

import static no.nav.varsel.jms.consumer.JmsConsumer.ConsumerNames.VARSEL_KVITTERING_NAME;
import static no.nav.varsel.jms.consumer.JmsConsumer.VARSEL_KVITTERING;

/**
 * Consumer for TVARSEL002 VarselKvittering
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Component
public class VarselKvitteringConsumer extends AbstractJmsConsumer<VarselKvittering> {

	private static final Logger log = LoggerFactory.getLogger(VarselKvitteringConsumer.class);

	private static final String VARSEL_KVITTERING_QUEUE = "varselKvitteringQueue";

	static final String TVARSEL002 = "tvarsel002";

	@Autowired
	private MottaVarselKvitteringMapper mottaVarselKvitteringMapper;
	@Autowired
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
	protected void handleMessage(ObjectMessageWrapper<VarselKvittering> wrappedKvittering) {
		VarselKvittering kvittering = wrappedKvittering.getObject();
		log.info("Mottatt kvittering med status=" + kvittering.getStatus() + ", og varselId=" + kvittering.getVarselId());
		try {
			MottaVarselKvitteringTo to = mottaVarselKvitteringMapper.map(kvittering);
			to.validateTo();
			mottaVarselKvitteringService.behandleKvitteringsmelding(to);
		} catch (IllegalArgumentException e) {
			throw new NoJmsBackoutException(e);
		}

	}
}
