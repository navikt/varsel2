package no.nav.varsel.repo;

import no.nav.varsel.repo.config.RepoTestConfig;
import no.nav.varsel.domain.Constants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Abstract class for repo tests
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@SpringBootTest(classes = RepoTestConfig.class)
@ActiveProfiles({"itest", "local"})
public abstract class AbstractRepoTest {

	@Autowired
	protected VarselbestillingRepo varselbestillingRepo;
	@Autowired
	protected VarselRepo varselRepo;
	@PersistenceContext
	protected EntityManager entityManager;

	@BeforeEach
	public void setUpAbstract() {
		MDC.put(Constants.USER_ID, "itest");
		varselbestillingRepo.deleteAll();
	}

	@AfterEach
	public void tearDownAbstract() {
		MDC.remove(Constants.USER_ID);
		varselbestillingRepo.deleteAll();
	}
}