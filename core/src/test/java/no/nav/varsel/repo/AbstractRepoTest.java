package no.nav.varsel.repo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import no.nav.varsel.domain.Constants;
import no.nav.varsel.repo.config.RepoTestConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = RepoTestConfig.class)
@AutoConfigureTestDatabase
@ActiveProfiles({"itest"})
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