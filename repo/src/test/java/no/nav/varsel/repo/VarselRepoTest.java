package no.nav.varsel.repo;

import org.junit.jupiter.api.Test;

import static no.nav.varsel.repo.TestdataUtil.VARSEL_ID;
import static no.nav.varsel.repo.TestdataUtil.createVarselbestilling;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Itest for Spring Data JPA {@link no.nav.varsel.domain.object.Varsel} Repo
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselRepoTest extends AbstractRepoTest {

	@Test
	public void shouldFindByVarselId() throws Exception {
		varselbestillingRepo.save(createVarselbestilling());

		assertThat(varselRepo.findByVarselId(VARSEL_ID), notNullValue());
	}

	@Test
	public void shouldPing() throws Exception {
		varselRepo.ping();
	}
}