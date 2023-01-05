package no.nav.varsel.repo;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class VarselRepoTest extends AbstractRepoTest {

	@Test
	public void shouldFindByVarselId() {
		varselbestillingRepo.save(TestdataUtil.createVarselbestilling());

		assertThat(varselRepo.findByVarselId(TestdataUtil.VARSEL_ID), notNullValue());
	}

}