package no.nav.varsel.kvarsel001;

import no.nav.doknotifikasjon.schemas.DoknotifikasjonStatus;
import no.nav.varsel.repo.VarselbestillingRepo;
import no.nav.varsel.service.support.exception.functional.StatusmeldingValidationException;
import no.nav.varsel.service.support.exception.functional.VarselbestillingNotExistException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jboss.logging.MDC;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static no.nav.varsel.kvarsel001.TestUtils.createDoknotifikasjonStatus;
import static no.nav.varsel.kvarsel001.TestUtils.createVarselbestilling;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = NotifikasjonStatusConsumerTest.class)
@ActiveProfiles("itest")
@Import({NotifikasjonStatusConsumer.class, RepoTestConfig.class})
class NotifikasjonStatusConsumerTest {

	private static final String TOPIC = "teamdokumenthandtering.aapen-dok-notifikasjon-status";

	@Autowired
	VarselbestillingRepo varselbestillingRepo;

	@Autowired
	NotifikasjonStatusConsumer notifikasjonStatusConsumer;

	@BeforeEach
	public void beforeEach() {
		MDC.put("userId", "kvarsel001-unit-test");
		varselbestillingRepo.save(createVarselbestilling());
	}

	@AfterEach
	public void afterEach() {
		varselbestillingRepo.deleteAll();
	}

	@Test
	void shouldFailOnUnexpectedBestillerId() {
		DoknotifikasjonStatus doknotifikasjonStatus = createDoknotifikasjonStatus();
		doknotifikasjonStatus.setBestillerId("Uventet bestillerId");

		ConsumerRecord<String, Object> statusRecord = new ConsumerRecord<>(
				TOPIC, 1, 0, "123", doknotifikasjonStatus);

		var e = assertThrows(StatusmeldingValidationException.class, () -> notifikasjonStatusConsumer.onMessage(statusRecord));
		assertTrue(e.getMessage().contains("ugyldig bestillerId"));
	}

	@Test
	void shouldFailOnMissingBestillingsId() {
		DoknotifikasjonStatus doknotifikasjonStatus = createDoknotifikasjonStatus();
		doknotifikasjonStatus.setBestillingsId(null);

		ConsumerRecord<String, Object> statusRecord = new ConsumerRecord<>(
				TOPIC, 1, 0, "123", doknotifikasjonStatus);

		var e = assertThrows(StatusmeldingValidationException.class, () -> notifikasjonStatusConsumer.onMessage(statusRecord));
		assertTrue(e.getMessage().contains("bestillingsId=null"));
	}

	@Test
	void shouldFailIfStatusIsNotFeiletEllerFerdigstilt() {
		DoknotifikasjonStatus doknotifikasjonStatus = createDoknotifikasjonStatus();
		doknotifikasjonStatus.setStatus("SENDT");

		ConsumerRecord<String, Object> statusRecord = new ConsumerRecord<>(
				TOPIC, 1, 0, "123", doknotifikasjonStatus);

		var e = assertThrows(StatusmeldingValidationException.class, () -> notifikasjonStatusConsumer.onMessage(statusRecord));
		assertTrue(e.getMessage().contains("ulik 'feilet' eller 'ferdigstilt'"));
	}

	@Test
	void shouldFailOnMissingVarselbestilling() {
		DoknotifikasjonStatus doknotifikasjonStatus = createDoknotifikasjonStatus();
		doknotifikasjonStatus.setBestillingsId(UUID.randomUUID().toString());

		ConsumerRecord<String, Object> statusRecord = new ConsumerRecord<>(
				TOPIC, 1, 0, "123", doknotifikasjonStatus);

		assertThrows(VarselbestillingNotExistException.class, () -> notifikasjonStatusConsumer.onMessage(statusRecord));
	}

}