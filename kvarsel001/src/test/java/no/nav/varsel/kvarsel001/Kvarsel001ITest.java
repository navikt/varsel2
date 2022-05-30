package no.nav.varsel.kvarsel001;

import no.nav.doknotifikasjon.schemas.DoknotifikasjonStatus;
import no.nav.varsel.domain.code.StatusCode;
import no.nav.varsel.repo.VarselRepo;
import no.nav.varsel.repo.VarselbestillingRepo;
import org.jboss.logging.MDC;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import static no.nav.varsel.kvarsel001.TestUtils.createDoknotifikasjonStatus;
import static no.nav.varsel.kvarsel001.TestUtils.createVarsel;
import static no.nav.varsel.kvarsel001.TestUtils.createVarselbestilling;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
@Import({NotifikasjonStatusConsumer.class, RepoTestConfig.class})
public class Kvarsel001ITest {

	private static final String TOPIC = "teamdokumenthandtering.aapen-dok-notifikasjon-status";

	private DoknotifikasjonStatus doknotifikasjonStatus;

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

}
