package no.nav.varsel.repo;

import static no.nav.varsel.repo.TestdataUtil.VARSELBESTILLING_ID;
import static no.nav.varsel.repo.TestdataUtil.createVarselbestilling;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import no.nav.varsel.domain.object.Varselbestilling;
import org.junit.Test;

/**
 * Itest for {@link VarselbestillingRepo}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselbestillingRepoTest extends AbstractRepoTest {

	@Test
	public void shouldSave() throws Exception {
		varselbestillingRepo.save(createVarselbestilling());

		assertThat(varselbestillingRepo.findAll(), hasSize(1));
	}

	@Test
	public void shouldFindyByVarselbestillingId() throws Exception {
		varselbestillingRepo.saveAndFlush(createVarselbestilling());

		assertThat(varselbestillingRepo.findByVarselbestillingId(VARSELBESTILLING_ID), notNullValue());
	}

	@Test
	public void shouldFindOneEager() throws Exception {
		varselbestillingRepo.saveAndFlush(createVarselbestilling());

		Varselbestilling varselbestilling = varselbestillingRepo.findByVarselbestillingIdEager(VARSELBESTILLING_ID);
		assertThat(varselbestilling, notNullValue());
		assertThat(varselbestilling.getVarsels(), notNullValue());
	}

	@Test
	public void shouldFindAllEager() throws Exception {
		varselbestillingRepo.saveAndFlush(createVarselbestilling());

		Varselbestilling varselbestilling = varselbestillingRepo.findAllEager().get(0);
		assertThat(varselbestilling, notNullValue());
		assertThat(varselbestilling.getVarsels(), notNullValue());
	}
}