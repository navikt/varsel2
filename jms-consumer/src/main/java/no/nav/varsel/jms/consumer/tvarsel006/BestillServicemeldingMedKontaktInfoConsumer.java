package no.nav.varsel.jms.consumer.tvarsel006;

import no.nav.melding.virksomhet.servicemeldingmedkontaktinformasjon.v1.servicemeldingmedkontaktinformasjon.ServicemeldingMedKontaktinformasjon;
import no.nav.varsel.jms.consumer.AbstractJmsConsumer;
import no.nav.varsel.jms.consumer.ObjectMessageWrapper;
import no.nav.varsel.jms.consumer.tvarsel006.support.BestillServicemeldingMedKontaktInfoMapper;
import no.nav.varsel.jms.to.xml.JmsReply;
import no.nav.varsel.service.ServicemeldingService;
import no.nav.varsel.service.to.BestillVarselTo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.jms.TextMessage;

import static no.nav.varsel.jms.consumer.JmsConsumer.BESTILL_SERVICEMELDING_KONTAKTINFO;
import static no.nav.varsel.jms.consumer.JmsConsumer.ConsumerNames.BESTILL_SERVICEMELDING_KONTAKTINFO_NAME;

/**
 * Consumer for TVARSEL006 ServiceMeldingMedKontaktInfo
 *
 * @author Roar Bjurstrom, Visma Consulting.
 */
@Component
public class BestillServicemeldingMedKontaktInfoConsumer extends AbstractJmsConsumer<ServicemeldingMedKontaktinformasjon> {

	private static final Logger log = LoggerFactory.getLogger(BestillServicemeldingMedKontaktInfoConsumer.class);

	private static final String SERVICEMELDING_KONTAKT_INFO_QUEUE = "bestillServicemeldingKontaktInfoQueue";

	@Inject
	private BestillServicemeldingMedKontaktInfoMapper mapper;
	@Inject
	private ServicemeldingService servicemeldingService;

	public BestillServicemeldingMedKontaktInfoConsumer() {
		super(BESTILL_SERVICEMELDING_KONTAKTINFO, ServicemeldingMedKontaktinformasjon.class);
	}

	@Override
	protected void handleMessage(ObjectMessageWrapper<ServicemeldingMedKontaktinformasjon> objectMessageWrapper) {
		BestillVarselTo to = mapper.map(objectMessageWrapper.getObject());
		log.info("bestillServiceMeldingMedKontaktInfo mottatt varselBestillingId={}, varselTypeId={}",
				to.getVarselBestillingId(), to.getVarseltypeId());
		to.validateTvarsel006Input();

		servicemeldingService.bestillServicemelding(to);
	}

	@Override
	@JmsListener(destination = SERVICEMELDING_KONTAKT_INFO_QUEUE, id = BESTILL_SERVICEMELDING_KONTAKTINFO_NAME)
	public JmsReply listen(TextMessage message) {
		return doListen(message);
	}
}
