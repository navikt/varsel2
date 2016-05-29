package no.nav.varsel.jms.consumer;

import no.nav.varsel.jms.config.JmsConsumerTestConfig;
import no.nav.varsel.jms.config.JmsTestConfig;
import no.nav.varsel.jms.to.xml.JmsReply;
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
import javax.jms.Message;
import javax.jms.Queue;

/**
 * Abstract Test for JMS
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = JmsConsumerTestConfig.class)
@ActiveProfiles("local")
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
}
