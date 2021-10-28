package no.nav.varsel.web;

import no.nav.varsel.config.BatchConfig;
import no.nav.varsel.config.JmsTestConfig;
import no.nav.varsel.config.RepoTestConfig;
import no.nav.varsel.config.WebConfig;
import no.nav.varsel.config.local.LocalTomcatConfiguration;
import no.nav.varsel.repo.VarselbestillingRepo;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;

/**
 * Abstract Test for Rest Itests
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureWireMock(port = 0)
@SpringBootTest(classes = {
		LocalTomcatConfiguration.class,
		BatchConfig.class,
		JmsTestConfig.class,
		RepoTestConfig.class,
		WebConfig.class
})
@ActiveProfiles({"itest", "local"})
public abstract class AbstractRestTest {

	@Inject
	protected VarselbestillingRepo varselbestillingRepo;
	@Inject
	protected WebApplicationContext webApplicationContext;
	protected MockMvc mockMvc;

	@BeforeClass
	public static void startup() throws Exception {
		JmsTestConfig.mockJndi();
	}

	@Before
	public final void setUpAbstract() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.build();
		cleanDb();
	}

	@After
	public final void tearDown() {
		cleanDb();
	}

	private void cleanDb() {
		varselbestillingRepo.deleteAll();
	}
}
