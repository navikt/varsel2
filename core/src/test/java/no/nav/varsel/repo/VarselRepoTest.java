package no.nav.varsel.repo;

import org.junit.jupiter.api.Test;

import static no.nav.varsel.repo.TestdataUtil.VARSEL_ID;
import static no.nav.varsel.repo.TestdataUtil.createVarselbestilling;
import static org.assertj.core.api.Assertions.assertThat;

public class VarselRepoTest extends AbstractRepoTest {

	@Test
	public void shouldFindByVarselId() {
		varselbestillingRepo.save(createVarselbestilling());

		assertThat(varselRepo.findByVarselId(VARSEL_ID)).isNotNull();
	}

}