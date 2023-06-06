package no.nav.varsel.kvarsel001;

import no.nav.doknotifikasjon.schemas.DoknotifikasjonStatus;
import no.nav.varsel.domain.code.StatusCode;
import no.nav.varsel.repo.VarselRepo;
import no.nav.varsel.repo.VarselbestillingRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import static no.nav.varsel.kvarsel001.TestUtils.createDoknotifikasjonStatus;
import static no.nav.varsel.kvarsel001.TestUtils.createVarsel;
import static no.nav.varsel.kvarsel001.TestUtils.createVarselbestilling;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(classes = Kvarsel001ITest.class)
@EmbeddedKafka(
		partitions = 1,
		topics = {
				"teamdokumenthandtering.aapen-dok-notifikasjon-status"
		},
		controlledShutdown = true,
		brokerProperties = {
				"listeners=PLAINTEXT://127.0.0.1:9092"
		}

)
@ActiveProfiles("itest")
@EnableAutoConfiguration
@Import(RepoTestConfig.class)
public class Kvarsel001ITest {

	private static final String TOPIC = "teamdokumenthandtering.aapen-dok-notifikasjon-status";

	private DoknotifikasjonStatus doknotifikasjonStatus;

	@Autowired
	DefaultErrorHandler spyableExponentialBackoffErrorhandler;

	@Autowired
	KafkaTemplate<String, Object> kafkaTemplate;

	@Autowired
	NotifikasjonStatusConsumer notifikasjonStatusConsumer;

	@Autowired
	VarselbestillingRepo varselbestillingRepo;

	@Autowired
	VarselRepo varselRepo;

	@BeforeAll
	static void beforeAll() {
		MDC.put("userId", "kvarsel001-test");
	}

	@BeforeEach
	void beforeEach() {
		var varselbestilling = varselbestillingRepo.save(createVarselbestilling());
		varselRepo.save(createVarsel(varselbestilling));
		doknotifikasjonStatus = createDoknotifikasjonStatus();
	}

	@AfterEach
	void afterEach() {
		varselbestillingRepo.deleteAll();
		varselRepo.deleteAll();
	}

	@Test
	public void shouldUpdateStatusToFerdigbehandlet() {
		doknotifikasjonStatus = createDoknotifikasjonStatus();
		kafkaTemplate.send(TOPIC, doknotifikasjonStatus);

		await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
			var result = varselRepo.findAll().get(0);

			assertEquals(StatusCode.FERDIGBEHANDLET, result.getStatus());
			assertNotNull(result.getDistribusjonTidspunkt());
		});
	}

	@Test
	public void shouldRetryAndBackoffOnNotExistingInDb() {
		varselbestillingRepo.deleteAll();
		varselRepo.deleteAll();
		kafkaTemplate.send(TOPIC, doknotifikasjonStatus);

		await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
			Mockito.verify(spyableExponentialBackoffErrorhandler, Mockito.times(4)).handleRemaining(any(), any(), any(), any());
		});

		var varselbestilling = varselbestillingRepo.save(createVarselbestilling());
		varselRepo.save(createVarsel(varselbestilling));

		await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
			var result = varselRepo.findAll().get(0);
			assertEquals(StatusCode.FERDIGBEHANDLET, result.getStatus());
			assertNotNull(result.getDistribusjonTidspunkt());
		});
	}
}
