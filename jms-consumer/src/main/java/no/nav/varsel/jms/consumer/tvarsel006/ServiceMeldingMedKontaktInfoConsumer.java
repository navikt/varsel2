package no.nav.varsel.jms.consumer.tvarsel006;

import static no.nav.varsel.jms.consumer.JmsConsumer.BESTILL_SERVICEMELDING_KONTAKTINFO;
import static no.nav.varsel.jms.consumer.JmsConsumer.ConsumerNames.BESTILL_SERVICEMELDING_KONTAKTINFO_NAME;

import no.nav.melding.virksomhet.servicemeldingmedkontaktinformasjon.v1.servicemeldingmedkontaktinformasjon.ServicemeldingMedKontaktinformasjon;
import no.nav.varsel.jms.consumer.AbstractJmsConsumer;
import no.nav.varsel.jms.consumer.ObjectMessageWrapper;
import no.nav.varsel.jms.consumer.tvarsel006.support.ServiceMeldingMedKontaktInfoMapper;
import no.nav.varsel.jms.to.xml.JmsReply;
import no.nav.varsel.service.tvarsel006.to.BestillServiceMeldingMedKontaktInfoTo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.jms.TextMessage;

/**
 * Consumer for TVARSEL006 ServiceMeldingMedKontaktInfo
 *
 * @author Roar Bjurstrom, Visma Consulting.
 */
@Component
public class ServiceMeldingMedKontaktInfoConsumer extends AbstractJmsConsumer<ServicemeldingMedKontaktinformasjon> {

	private static final Logger LOGG = LoggerFactory.getLogger(ServiceMeldingMedKontaktInfoConsumer.class);

	private static final String SERVICEMELDING_KONTAKT_INFO_QUEUE = "bestillServicemeldingKontaktInfoQueue";

	@Inject
	private ServiceMeldingMedKontaktInfoMapper mapper;

	public ServiceMeldingMedKontaktInfoConsumer() {
		super(BESTILL_SERVICEMELDING_KONTAKTINFO, ServicemeldingMedKontaktinformasjon.class);
	}

	@Override
	protected void handleMessage(ObjectMessageWrapper<ServicemeldingMedKontaktinformasjon> objectMessageWrapper) {
		BestillServiceMeldingMedKontaktInfoTo to = mapper.map(objectMessageWrapper.getObject());
		to.validateTvarsel006Input();

		//TODO Create service
	}

	@Override
	@JmsListener(destination = SERVICEMELDING_KONTAKT_INFO_QUEUE, id = BESTILL_SERVICEMELDING_KONTAKTINFO_NAME)
	public JmsReply listen(TextMessage message) {
		return doListen(message);
	}
}
