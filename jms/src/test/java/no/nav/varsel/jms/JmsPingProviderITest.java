package no.nav.varsel.jms;

import no.nav.varsel.config.JmsTestConfig;
import no.nav.varsel.config.support.QueueInfo;
import no.nav.varsel.domain.to.Ping;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.Queue;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

/**
 * Itest for jms pinger
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = JmsTestConfig.class)
@ActiveProfiles({"itest"})
public class JmsPingProviderITest {

	@Inject
	private JmsPingProvider jmsPingProvider;

	@Resource
	private Map<QueueInfo, Queue> queueOverview;

	@BeforeClass
	public static void setUpStatic() throws Exception {
		JmsTestConfig.mockJndi();
	}

	@Test
	public void shouldStartcontext() throws Exception {

	}

	@Test
	public void shouldPing() throws Exception {
		List<Ping> ping = jmsPingProvider.ping();

		assertThat(ping, hasSize(queueOverview.size()));
		ping.forEach(p -> {
			assertThat(p.getType(), anyOf(is(Ping.Type.Queue), is(Ping.Type.RemoteQueue)));
			assertThat(p.getAddress(), startsWith("mq_"));
			assertThat(p.getBeskrivelse(), notNullValue());
			assertThat(p.getName(), notNullValue());
			if (p.getType() != Ping.Type.RemoteQueue) {
				p.getPinger().run();
			}
		});
	}
}