package no.nav.varsel.jms.consumer;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import no.nav.varsel.config.JmsConsumerTestConfig;
import no.nav.varsel.config.JmsTestConfig;
import no.nav.varsel.jms.to.xml.JmsReply;
import no.nav.varsel.repo.VarselRepo;
import no.nav.varsel.repo.VarselbestillingRepo;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Abstract Test for JMS
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = JmsConsumerTestConfig.class)
@ActiveProfiles({"itest"})
@DirtiesContext
public abstract class AbstractConsumerJmsTest {

	@BeforeClass
	public static void setUpStatic() throws Exception {
		JmsTestConfig.mockJndi();
	}

	@Inject
	protected JmsTemplate jmsTemplate;
	@Inject
	protected Queue replyQueue;
	@Inject
	protected Queue backoutQueue;

	@Inject
	protected VarselbestillingRepo varselbestillingRepo;
	@Inject
	protected VarselRepo varselRepo;

	@Before
	public void setUpAbstract() throws Exception {
		varselbestillingRepo.deleteAll();
	}

	@After
	public void tearDownAbstract() throws Exception {
		varselbestillingRepo.deleteAll();
	}

	protected JmsReply sendMessage(Queue queue, Object message) {
		jmsTemplate.convertAndSend(queue, message, message1 -> {
			message1.setJMSReplyTo(replyQueue);
			return message1;
		});
		return (JmsReply) jmsTemplate.receiveAndConvert(replyQueue);
	}

	protected Message sendMessageListenBoq(Queue queue, Object message) {
		jmsTemplate.convertAndSend(queue, message);
		return jmsTemplate.receive(backoutQueue);
	}

	protected void isOk(JmsReply response) {
		assertTrue(response != null && response.isOk());
	}

	protected void isOk(Message response) {
		assertThat(response, notNullValue());
	}

}
