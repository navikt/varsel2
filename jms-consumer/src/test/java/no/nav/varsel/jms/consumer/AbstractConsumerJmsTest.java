package no.nav.varsel.jms.consumer;

import com.github.tomakehurst.wiremock.client.WireMock;
import jakarta.jms.Message;
import jakarta.jms.Queue;
import jakarta.xml.bind.JAXBElement;
import no.nav.melding.virksomhet.varsel.v1.varsel.XMLVarsel;
import no.nav.varsel.config.JmsConsumerTestConfig;
import no.nav.varsel.jms.to.xml.JmsReply;
import no.nav.varsel.repo.VarselRepo;
import no.nav.varsel.repo.VarselbestillingRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest(classes = JmsConsumerTestConfig.class)
@ActiveProfiles({"itest", "local"})
@ComponentScan
@AutoConfigureWireMock(port = 0)
public abstract class AbstractConsumerJmsTest {

	public static final String FEIL_MQ_UT = "feil_mq_ut";

	@Autowired
	protected JmsTemplate jmsTemplate;

	@Autowired
	protected Queue bestillServicemeldingQueue;

	@Autowired
	protected Queue backoutQueue;

	@Autowired
	protected VarselbestillingRepo varselbestillingRepo;

	@Autowired
	protected VarselRepo varselRepo;

	@Autowired
	private TransactionTemplate transactionTemplate;

	@BeforeEach
	public void setUpAbstract() {
		this.stubStsConsumer();
	}

	@AfterEach
	public void tearDownAbstract() {
		WireMock.removeAllMappings();
		WireMock.resetAllRequests();
		varselbestillingRepo.deleteAll();
	}

	protected JmsReply sendMessage(Queue queue, JAXBElement<?> message) {
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
				jmsTemplate.convertAndSend(queue, message, message1 -> {
					message1.setJMSReplyTo(bestillServicemeldingQueue);
					return message1;
				});
			}
		});
		return transactionTemplate.execute(transactionStatus -> receive(bestillServicemeldingQueue));
	}

	protected void sendMessageNoReply(Queue queue, JAXBElement<?> message) {
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
				jmsTemplate.convertAndSend(queue, message);
			}
		});
	}

	@SuppressWarnings("unchecked")
	protected <T> T receive(Queue queue) {
		Object response = transactionTemplate.execute(transactionStatus -> jmsTemplate.receiveAndConvert(queue));
		if (response instanceof JAXBElement) {
			response = ((JAXBElement) response).getValue();
		} else if (response instanceof JmsReply || response instanceof XMLVarsel) {
			return (T) response;
		} else {
			if (response == null) {
				return null;
			} else {
				throw new UnsupportedOperationException("receive: response er ikke en håndterbar type. type=" + response.getClass());
			}
		}
		return (T) response;
	}

	protected Message sendMessageListenBoq(Queue queue, JAXBElement<?> message) {
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
				jmsTemplate.convertAndSend(queue, message);
			}
		});
		return transactionTemplate.execute(t -> jmsTemplate.receive(backoutQueue));
	}

	protected void isOk(JmsReply response) {
		assertTrue(response != null && response.isOk());
	}

	protected void isError(JmsReply response) {
		assertTrue(response != null && !response.isOk());
	}

	protected void isOk(Message response) {
		assertThat(response, notNullValue());
	}

	public void stubVarselInfoV1() {
		stubFor(get("/no/nav/varsel/rest/varselInfoV1/varseltypeId")
				.willReturn(aResponse()
						.withStatus(OK.value())
						.withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
						.withBodyFile("varselInfoV1/varselInfoV1-happy.json")));
	}

	public void stubVarselInfoV1VarselFeil() {
		stubFor(get("/no/nav/varsel/rest/varselInfoV1/varsel_test_feil")
				.willReturn(aResponse()
						.withStatus(OK.value())
						.withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
						.withBodyFile("varselInfoV1/varsel-test-feil.json")));
	}

	public void stubVarselInfoV1VarselURL() {
		stubFor(get("/no/nav/varsel/rest/varselInfoV1/varsel_varselUrl")
				.willReturn(aResponse()
						.withStatus(OK.value())
						.withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
						.withBodyFile("varselInfoV1/varsel-varsel-url.json")));
	}

	public void stubVarselInfoV1VarselMissing() {
		stubFor(get("/no/nav/varsel/rest/varselInfoV1/varsel_missing")
				.willReturn(aResponse()
						.withStatus(OK.value())
						.withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
						.withBodyFile("varselInfoV1/varsel-missing.json")));
	}

	public void stubStsConsumer() {
		stubFor(get("/securitytoken?grant_type=client_credentials&scope=openid")
				.willReturn(aResponse()
						.withStatus(OK.value())
						.withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
						.withBodyFile("sts/stsResponse-happy.json")));
	}

	public void stubPdlConsumer() {
		stubFor(post("/pdl")
				.willReturn(aResponse()
						.withStatus(OK.value())
						.withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
						.withBodyFile("__/files/pdl/pdl-aktoerid-happy.json")));
	}

	public void stubPdlConsumerNotFound() {
		stubFor(post("/pdl")
				.willReturn(aResponse()
						.withStatus(OK.value())
						.withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
						.withBodyFile("pdl/pdl-ident-notfound.json")));
	}

	public void stubPdlConsumerServerError() {
		stubFor(post("/pdl")
				.willReturn(aResponse()
						.withStatus(OK.value())
						.withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
						.withBodyFile("pdl/pdl-ident-server-error.json")));
	}

	public void stubPdlConsumerTechnicalErrorWithInternalServerError() {
		stubFor(post("/pdl")
				.willReturn(aResponse()
						.withStatus(INTERNAL_SERVER_ERROR.value())
						.withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)));
	}

	public void stubPdlConsumerFunctionalErrorWithInternalServerError() {
		stubFor(post("/pdl")
				.willReturn(aResponse()
						.withStatus(BAD_REQUEST.value())
						.withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)));
	}
}
