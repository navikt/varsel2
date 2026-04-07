package no.nav.varsel.kvarsel001;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.doknotifikasjon.schemas.DoknotifikasjonStatus;
import no.nav.varsel.domain.code.StatusCode;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.exception.functional.StatusmeldingMappingException;
import no.nav.varsel.exception.functional.StatusmeldingValidationException;
import no.nav.varsel.exception.functional.VarselbestillingNotExistException;
import no.nav.varsel.repo.VarselbestillingRepo;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static no.nav.varsel.kvarsel001.StatusmeldingValidator.validerStatusmelding;
import static no.nav.varsel.util.MDCGenerate.clearCallId;
import static no.nav.varsel.util.MDCGenerate.clearUserId;
import static no.nav.varsel.util.MDCGenerate.generateCallId;
import static no.nav.varsel.util.MDCGenerate.setUserId;

@Slf4j
@Component
public class NotifikasjonStatusConsumer {

	public static final String KAFKA_TOPIC_DOK_NOTIFIKASJON_STATUS = "teamdokumenthandtering.aapen-dok-notifikasjon-status";
	private static final String FEILET = "FEILET";
	private static final String FERDIGSTILT = "FERDIGSTILT";

	private final ObjectMapper objectMapper;
	private final VarselbestillingRepo varselbestillingRepo;

	public NotifikasjonStatusConsumer(ObjectMapper objectMapper, VarselbestillingRepo varselbestillingRepo) {
		this.objectMapper = objectMapper;
		this.varselbestillingRepo = varselbestillingRepo;
	}

	@KafkaListener(
			topics = KAFKA_TOPIC_DOK_NOTIFIKASJON_STATUS,
			groupId = "varsel-kvarsel001"
	)
	public void onMessage(final ConsumerRecord<String, Object> record) {
		setUserIdAndGenerateCallId();

		log.info("Innkommende Kafka-record til topic={}, partition={}, offset={}", record.topic(), record.partition(), record.offset());

		try {
			DoknotifikasjonStatus doknotifikasjonStatus = objectMapper.readValue(record.value().toString(), DoknotifikasjonStatus.class);

			log.info("Mottok statusmelding med bestillingsId={}, bestillerId={}, status={}, melding={}, distribusjonId={}",
					doknotifikasjonStatus.getBestillingsId(),
					doknotifikasjonStatus.getBestillerId(),
					doknotifikasjonStatus.getStatus(),
					doknotifikasjonStatus.getMelding(),
					doknotifikasjonStatus.getDistribusjonId());

			if (!validerStatusmelding(doknotifikasjonStatus)) return;

			Varselbestilling varselbestilling = finnVarselbestilling(doknotifikasjonStatus);
			oppdaterStatusForVarsler(doknotifikasjonStatus, varselbestilling);
			varselbestillingRepo.saveAndFlush(varselbestilling);

			log.info("Varsler i bestillingsId={} oppdatert til status={}", doknotifikasjonStatus.getBestillingsId(), doknotifikasjonStatus.getStatus());
		} catch (JacksonException e) {
			log.error("Mapping av statusmelding (kvarsel001) feilet med melding={}", e.getMessage());
			throw new StatusmeldingMappingException(e.getMessage(), e.getCause());
		} catch (StatusmeldingValidationException e) {
			throw e;
		} catch (VarselbestillingNotExistException e) {
			log.warn("{} Avbryter behandling.", e.getMessage());
		} catch (Exception e) {
			log.error("Ukjent teknisk feil under prosessering av statusmelding (kvarsel001). Konsumerer hendelse på nytt.", e);
			throw e;
		} finally {
			clearUserIdAndCallId();
		}
	}


	private Varselbestilling finnVarselbestilling(DoknotifikasjonStatus doknotifikasjonStatus) {
		Varselbestilling varselbestilling = varselbestillingRepo.findByVarselbestillingIdEager(doknotifikasjonStatus.getBestillingsId());

		if (varselbestilling == null) {
			throw new VarselbestillingNotExistException(doknotifikasjonStatus.getBestillingsId());
		}

		return varselbestilling;
	}

	private void oppdaterStatusForVarsler(DoknotifikasjonStatus doknotifikasjonStatus, Varselbestilling varselbestilling) {
		var tidspunkt = LocalDateTime.now();

		varselbestilling.getVarsels().forEach(varsel -> {
			if (FEILET.equals(doknotifikasjonStatus.getStatus())) {
				varsel.setStatus(StatusCode.FEILET);
				varsel.setFeilbeskrivelse(doknotifikasjonStatus.getMelding());
			}

			if (FERDIGSTILT.equals(doknotifikasjonStatus.getStatus())) {
				varsel.setStatus(StatusCode.FERDIGBEHANDLET);
				varsel.setDistribusjonTidspunkt(tidspunkt);
			}

			varsel.setKvitteringTidspunkt(tidspunkt);
		});
	}

	private void setUserIdAndGenerateCallId() {
		generateCallId();
		setUserId("kvarsel001");
	}

	private void clearUserIdAndCallId() {
		clearUserId();
		clearCallId();
	}
}
