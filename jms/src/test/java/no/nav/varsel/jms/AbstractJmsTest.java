package no.nav.varsel.jms;

import no.nav.varsel.jms.consumer.ConsumerManager;
import no.nav.varsel.jms.consumer.config.JmsTestConfig;
import no.nav.varsel.jms.to.JmsReply;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.jms.Queue;

/**
 * Abstract Test for JMS
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = JmsTestConfig.class)
@ActiveProfiles("local")
@DirtiesContext
public abstract class AbstractJmsTest {

	@Inject
	protected ConsumerManager consumerManager;
	@Inject
	protected JmsTemplate jmsTemplate;
	@Inject
	protected Queue reply;

	protected JmsReply sendMessage(Queue queue, Object message) {
		jmsTemplate.convertAndSend(queue, message, message1 -> {
			message1.setJMSReplyTo(reply);
			return message1;
		});
		return (JmsReply) jmsTemplate.receiveAndConvert(reply);
	}
}
