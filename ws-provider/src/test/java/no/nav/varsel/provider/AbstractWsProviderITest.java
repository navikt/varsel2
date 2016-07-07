package no.nav.varsel.provider;

import no.nav.varsel.config.JmsTestConfig;
import no.nav.varsel.config.WsProviderTestConfig;
import no.nav.varsel.domain.Constants;
import no.nav.varsel.repo.VarselRepo;
import no.nav.varsel.repo.VarselbestillingRepo;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.slf4j.MDC;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

/**
 * Abstract for Ws Provider Itests
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WsProviderTestConfig.class)
@ActiveProfiles({"itest"})
@DirtiesContext
public abstract class AbstractWsProviderITest {

	@Inject
	protected VarselbestillingRepo varselbestillingRepo;
	@Inject
	protected VarselRepo varselRepo;


	@BeforeClass
	public static void setUpStatic() throws Exception {
		JmsTestConfig.mockJndi();
	}

	@Before
	public void setUpAbstract() throws Exception {
		varselbestillingRepo.deleteAll();
		MDC.put(Constants.USER_ID, "wsprovitest");
	}

	@After
	public void tearDownAbstract() throws Exception {
		varselbestillingRepo.deleteAll();
		MDC.remove(Constants.USER_ID);
	}
}
