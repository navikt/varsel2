package no.nav.varsel.repo;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

import org.hibernate.SessionFactory;
import org.junit.Test;

import javax.inject.Inject;

/**
 * Itest for context
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class RepoContextTest extends AbstractRepoTest {

	@Inject
	private SessionFactory sessionFactory;
	@Inject
	private SessionFactory nonxaSessionFactory;

	@Test
	public void shouldCreateDifferentSessionFactories() throws Exception {
		assertThat(sessionFactory, is(not(nonxaSessionFactory)));
	}
}