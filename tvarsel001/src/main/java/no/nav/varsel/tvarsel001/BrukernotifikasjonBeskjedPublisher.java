package no.nav.varsel.tvarsel001;

import lombok.extern.slf4j.Slf4j;
import no.nav.brukernotifikasjon.schemas.input.BeskjedInput;
import no.nav.brukernotifikasjon.schemas.input.NokkelInput;
import no.nav.varsel.kafka.KafkaEventProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Import(KafkaEventProducer.class)
public class BrukernotifikasjonBeskjedPublisher {

	private final KafkaEventProducer kafkaEventProducer;
	private final String topic;

	public BrukernotifikasjonBeskjedPublisher(KafkaEventProducer kafkaEventProducer,
											  @Value("${brukernotifikasjon.beskjed.topic}") String topic) {
		this.kafkaEventProducer = kafkaEventProducer;
		this.topic = topic;
	}

	public void sendNotifikasjon(BeskjedInput beskjedInput, NokkelInput nokkelInput) {
		log.info("BeskjedInput: tidspunkt={}, synligFremTil={}, tekst={}, link={}, sikkerhetsnivaa={}, eksternVarsling={}, prefererteKanaler={}, epostVarslingstekst={}, epostVarslingstittel={}, smsVarslingstekst={}",
				beskjedInput.getTidspunkt(),
				beskjedInput.getSynligFremTil(),
				beskjedInput.getTekst(),
				beskjedInput.getLink(),
				beskjedInput.getSikkerhetsnivaa(),
				beskjedInput.getEksternVarsling(),
				beskjedInput.getPrefererteKanaler(),
				beskjedInput.getEpostVarslingstekst(),
				beskjedInput.getEpostVarslingstittel(),
				beskjedInput.getSmsVarslingstekst());

		log.info("NokkelInput: eventId={}, grupperingsId={}, fodselsnummer={}, namespace={}, appnavn={}",
				nokkelInput.getEventId(),
				nokkelInput.getGrupperingsId(),
				nokkelInput.getFodselsnummer(),
				nokkelInput.getNamespace(),
				nokkelInput.getAppnavn());

		log.info("Sender brukernotifikasjon med bestillingsId={} til topic={}", nokkelInput.getEventId(), topic);
		kafkaEventProducer.publish(topic, nokkelInput, beskjedInput);
	}
}
