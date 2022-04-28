package no.nav.varsel.tvarsel001;

import lombok.extern.slf4j.Slf4j;
import no.nav.doknotifikasjon.schemas.Doknotifikasjon;
import no.nav.varsel.kafka.KafkaEventProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Import(KafkaEventProducer.class)
public class NotifikasjonPublisher {

	private final KafkaEventProducer kafkaEventProducer;
	private final String topic;

	public NotifikasjonPublisher(KafkaEventProducer kafkaEventProducer,
								 @Value("${privat-dok-notifikasjon.topic}") String topic) {
		this.kafkaEventProducer = kafkaEventProducer;
		this.topic = topic;
	}

	public void sendNotifikasjon(Doknotifikasjon doknotifikasjon) {
		log.info("""
				bestillingsId={},
				bestillerId={},
				sikkerhetsnivaa={},
				fodselsnummer={},
				antallRenotifikasjoner={},
				renotifikasjonIntervall={},
				tittel={},
				epostTekst={},
				smsTekst={},
				prefererteKanaler={}""",
				doknotifikasjon.getBestillingsId(), doknotifikasjon.getBestillerId(), doknotifikasjon.getSikkerhetsnivaa(), doknotifikasjon.getFodselsnummer(), doknotifikasjon.getAntallRenotifikasjoner(), doknotifikasjon.getRenotifikasjonIntervall(), doknotifikasjon.getTittel(), doknotifikasjon.getEpostTekst(), doknotifikasjon.getSmsTekst(), doknotifikasjon.getPrefererteKanaler());

		log.info("Sender notifikasjon med bestillingsId={} til topic={}", doknotifikasjon.getBestillingsId(), topic);
		kafkaEventProducer.publish(topic, doknotifikasjon.getBestillingsId(), doknotifikasjon);
	}
}
