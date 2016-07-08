package no.nav.varsel.repo;

import no.nav.varsel.config.RepoTestConfig;
import no.nav.varsel.domain.Constants;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.MDC;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;


/**
 * Abstract class for repo tests
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RepoTestConfig.class)
@ActiveProfiles({"itest"})
public abstract class AbstractRepoTest {

	@Inject
	protected VarselbestillingRepo varselbestillingRepo;
	@Inject
	protected VarselRepo varselRepo;

	@Before
	public void setUpAbstract() throws Exception {
		MDC.put(Constants.USER_ID, "itest");
		varselbestillingRepo.deleteAll();
	}

	@After
	public void tearDownAbstract() throws Exception {
		MDC.remove(Constants.USER_ID);
		varselbestillingRepo.deleteAll();
	}
}