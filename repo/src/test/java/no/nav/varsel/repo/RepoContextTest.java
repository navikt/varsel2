package no.nav.varsel.repo;

import org.hibernate.SessionFactory;
import org.junit.Test;

import javax.inject.Inject;
import javax.inject.Named;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

/**
 * Itest for context
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class RepoContextTest extends AbstractRepoTest {

	@Inject
	private SessionFactory sessionFactory;
	@Inject
	@Named("nonxaSessionFactory")
	private SessionFactory nonxaSessionFactory;

	@Test
	public void shouldCreateDifferentSessionFactories() throws Exception {
		assertThat(sessionFactory, is(not(nonxaSessionFactory)));
	}
}