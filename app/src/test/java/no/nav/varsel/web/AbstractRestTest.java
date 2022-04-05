package no.nav.varsel.web;

import no.nav.varsel.config.JmsTestConfig;
import no.nav.varsel.config.RepoTestConfig;
import no.nav.varsel.repo.VarselbestillingRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Abstract Test for Rest Itests
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@AutoConfigureWireMock(port = 0)
@SpringBootTest(classes = {
		JmsTestConfig.class,
		RepoTestConfig.class
})
@ActiveProfiles({"itest", "local"})
public abstract class AbstractRestTest {

	@Autowired
	protected VarselbestillingRepo varselbestillingRepo;
	@Autowired
	protected WebApplicationContext webApplicationContext;
	protected MockMvc mockMvc;

	@BeforeEach
	public final void setUpAbstract() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.build();
		cleanDb();
	}

	@AfterEach
	public final void tearDown() {
		cleanDb();
	}

	private void cleanDb() {
		varselbestillingRepo.deleteAll();
	}
}
