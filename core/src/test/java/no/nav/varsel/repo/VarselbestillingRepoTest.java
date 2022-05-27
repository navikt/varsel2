package no.nav.varsel.repo;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

import no.nav.varsel.domain.object.Varselbestilling;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Itest for {@link VarselbestillingRepo}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselbestillingRepoTest extends AbstractRepoTest {

	@Test
	public void shouldSave() throws Exception {
		varselbestillingRepo.save(TestdataUtil.createVarselbestilling());

		MatcherAssert.assertThat(varselbestillingRepo.findAll(), hasSize(1));
	}

	@Test
	public void shouldFindyByVarselbestillingId() throws Exception {
		varselbestillingRepo.saveAndFlush(TestdataUtil.createVarselbestilling());

		MatcherAssert.assertThat(varselbestillingRepo.findByVarselbestillingId(TestdataUtil.VARSELBESTILLING_ID), notNullValue());
	}

	@Test
	public void shouldFindyByVarselbestillingIdWhenNoVarsel() throws Exception {
		Varselbestilling varselbestilling = TestdataUtil.createVarselbestilling();
		varselbestilling.getVarsels().clear();
		varselbestillingRepo.saveAndFlush(varselbestilling);

		MatcherAssert.assertThat(varselbestillingRepo.findByVarselbestillingId(TestdataUtil.VARSELBESTILLING_ID), notNullValue());
	}

	@Test
	public void shouldFindOneEager() throws Exception {
		varselbestillingRepo.saveAndFlush(TestdataUtil.createVarselbestilling());

		Varselbestilling varselbestilling = varselbestillingRepo.findByVarselbestillingIdEager(TestdataUtil.VARSELBESTILLING_ID);
		assertThat(varselbestilling, notNullValue());
		assertThat(varselbestilling.getVarsels(), notNullValue());
	}

	@Test
	public void shouldFindOneEagerWhenNoVarsel() throws Exception {
		Varselbestilling varselbestilling = TestdataUtil.createVarselbestilling();
		varselbestilling.getVarsels().clear();
		varselbestillingRepo.saveAndFlush(varselbestilling);

		varselbestilling = varselbestillingRepo.findByVarselbestillingIdEager(TestdataUtil.VARSELBESTILLING_ID);
		assertThat(varselbestilling, notNullValue());
		assertThat(varselbestilling.getVarsels(), notNullValue());
	}

	@Test
	public void shouldFindAllEager() throws Exception {
		varselbestillingRepo.saveAndFlush(TestdataUtil.createVarselbestilling());

		List<Varselbestilling> allEager = varselbestillingRepo.findAllEager();
		assertThat(allEager, hasSize(1));
		Varselbestilling varselbestilling = allEager.get(0);
		assertThat(varselbestilling, notNullValue());
		assertThat(varselbestilling.getVarsels(), notNullValue());
	}

	@Test
	public void shouldFindAllEagerWhenNoVarsel() throws Exception {
		Varselbestilling varselbestilling = TestdataUtil.createVarselbestilling();
		varselbestilling.getVarsels().clear();
		varselbestillingRepo.saveAndFlush(varselbestilling);

		List<Varselbestilling> allEager = varselbestillingRepo.findAllEager();
		assertThat(allEager, hasSize(1));
		varselbestilling = allEager.get(0);
		assertThat(varselbestilling, notNullValue());
		assertThat(varselbestilling.getVarsels(), notNullValue());
	}
}