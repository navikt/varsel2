package no.nav.varsel.jms.consumer.config.tvarsel001;

import no.nav.melding.virksomhet.varsel.v1.varsel.Varsel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Consumer for TVARSEL001 BestillServicemelding
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Component
public class BestillServicemeldingConsumer {
	public static final Logger LOGG = LoggerFactory.getLogger(BestillServicemeldingConsumer.class);

	@JmsListener(destination = "bestillServiceMelding")
	public void bestillMelding(@Payload Varsel varsel) {
		LOGG.info(varsel.getVarslingstype().toString());
	}
}
