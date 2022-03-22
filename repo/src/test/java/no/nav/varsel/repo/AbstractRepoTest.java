package no.nav.varsel.repo;

import no.nav.varsel.config.RepoTestConfig;
import no.nav.varsel.domain.Constants;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Abstract class for repo tests
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RepoTestConfig.class)
@ActiveProfiles({"itest", "local"})
public abstract class AbstractRepoTest {

	@Autowired
	protected VarselbestillingRepo varselbestillingRepo;
	@Autowired
	protected VarselRepo varselRepo;
	@PersistenceContext
	protected EntityManager entityManager;

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