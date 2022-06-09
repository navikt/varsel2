package no.nav.varsel.jms.consumer;

import no.nav.melding.virksomhet.varselutsending.v2.varselutsending.Varselutsending;
import no.nav.varsel.config.JmsConsumerTestConfig;
import no.nav.varsel.jms.to.xml.JmsReply;
import no.nav.varsel.repo.VarselRepo;
import no.nav.varsel.repo.VarselbestillingRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.jms.Message;
import javax.jms.Queue;
import javax.xml.bind.JAXBElement;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = JmsConsumerTestConfig.class)
@ActiveProfiles({"itest", "local"})
@AutoConfigureWireMock(port = 0)
public abstract class AbstractConsumerJmsTest {

	public static final String FEIL_MQ_UT = "feil_mq_ut";

	@Autowired
	protected JmsTemplate jmsTemplate;

	@Autowired
	protected Queue replyQueue;

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
		this.stubPdlConsumer();
		this.stubVarselInfoV1();
		this.stubVarselInfoV1VarselFeil();
		this.stubVarselInfoV1VarselURL();
		this.stubVarselInfoV1VarselMissing();
		varselbestillingRepo.deleteAll();
	}

	@AfterEach
	public void tearDownAbstract() {
		varselbestillingRepo.deleteAll();
	}

	protected JmsReply sendMessage(Queue queue, JAXBElement<?> message) {
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
				jmsTemplate.convertAndSend(queue, message, message1 -> {
					message1.setJMSReplyTo(replyQueue);
					return message1;
				});
			}
		});
		return transactionTemplate.execute(transactionStatus -> receive(replyQueue));
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
		} else if(response instanceof JmsReply) {
			return (T) response;
		} else {
			if(response == null) {
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

	protected Varselutsending findLastMessage(Queue varselutsendingQueue) {
		Varselutsending varselutsending = null;
		Varselutsending lastMessage = null;
		while((varselutsending = receive(varselutsendingQueue))!=null){
			lastMessage = varselutsending;
		}
		return lastMessage;
	}


	public void stubVarselInfoV1() {
		stubFor(get("/no/nav/varsel/rest/varselInfoV1/varseltypeId")
				.willReturn(aResponse().withStatus(OK.value())
						.withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
						.withBodyFile("varselInfoV1/varselInfoV1-happy.json")));
	}

	public void stubVarselInfoV1VarselFeil() {
		stubFor(get("/no/nav/varsel/rest/varselInfoV1/varsel_test_feil")
				.willReturn(aResponse().withStatus(OK.value())
						.withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
						.withBodyFile("varselInfoV1/varsel-test-feil.json")));
	}

	public void stubVarselInfoV1VarselURL() {
		stubFor(get("/no/nav/varsel/rest/varselInfoV1/varsel_varselUrl")
				.willReturn(aResponse().withStatus(OK.value())
						.withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
						.withBodyFile("varselInfoV1/varsel-varsel-url.json")));
	}

	public void stubVarselInfoV1VarselMissing() {
		stubFor(get("/no/nav/varsel/rest/varselInfoV1/varsel_missing")
				.willReturn(aResponse().withStatus(OK.value())
						.withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
						.withBodyFile("varselInfoV1/varsel-missing.json")));
	}

	public void stubStsConsumer() {
		stubFor(get("/securitytoken?grant_type=client_credentials&scope=openid")
				.willReturn(aResponse().withStatus(OK.value())
						.withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
						.withBodyFile("sts/stsResponse-happy.json")));
	}

	public void stubPdlConsumer() {
		stubFor(post("/pdl")
				.willReturn(aResponse().withStatus(OK.value())
						.withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
						.withBodyFile("pdl/pdl-aktoerid-happy.json")));
	}

	public void stubPdlConsumerNotFound() {
		stubFor(post("/pdl")
				.willReturn(aResponse().withStatus(OK.value())
						.withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
						.withBodyFile("pdl/pdl-ident-notfound.json")));
	}

	public void stubPdlConsumerServerError() {
		stubFor(post("/pdl")
				.willReturn(aResponse().withStatus(OK.value())
						.withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
						.withBodyFile("pdl/pdl-ident-server-error.json")));
	}

	public void stubPdlConsumerTechnicalErrorWithInternalServerError() {
		stubFor(post("/pdl")
				.willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR.value())
						.withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())));
	}

	public void stubPdlConsumerFunctionalErrorWithInternalServerError() {
		stubFor(post("/pdl")
				.willReturn(aResponse().withStatus(BAD_REQUEST.value())
						.withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())));
	}
}
