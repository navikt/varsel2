package no.nav.varsel.web;

import no.nav.varsel.repo.VarselbestillingRepo;
import no.nav.varsel.repo.config.RepoTestConfig;
import no.nav.varsel.tvarsel001.jms.config.JmsTestConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@EnableWireMock()
@SpringBootTest(classes = {
		JmsTestConfig.class,
		RepoTestConfig.class
})
@ActiveProfiles({"itest"})
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
