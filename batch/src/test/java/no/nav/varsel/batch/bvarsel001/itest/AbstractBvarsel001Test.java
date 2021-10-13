package no.nav.varsel.batch.bvarsel001.itest;

import no.nav.varsel.batch.AbstractBatchTest;
import no.nav.varsel.repo.batch.Bvarsel001Repo;
import org.junit.Before;
import org.springframework.batch.core.Job;

import javax.inject.Inject;
import javax.jms.Queue;

public abstract class AbstractBvarsel001Test extends AbstractBatchTest {

	@Inject
	protected Bvarsel001Repo bvarsel001Repo;

	@Inject
	protected Queue bestillVarselQueue;

	@Override
	@Inject
	public void setJob(Job bvarsel001Job) {
		super.setJob(bvarsel001Job);
	}

	@Before
	public void setUpAbstractBvarsel001() {
		bvarsel001Repo.deleteAll();
	}
}
