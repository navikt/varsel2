package no.nav.varsel.repo;

import no.nav.varsel.domain.object.Varselbestilling;
import org.junit.jupiter.api.Test;

import java.util.List;

import static no.nav.varsel.repo.TestdataUtil.VARSELBESTILLING_ID;
import static no.nav.varsel.repo.TestdataUtil.createVarselbestilling;
import static org.assertj.core.api.Assertions.assertThat;

public class VarselbestillingRepoTest extends AbstractRepoTest {

	@Test
	public void shouldSave() {
		varselbestillingRepo.save(createVarselbestilling());

		assertThat(varselbestillingRepo.findAll()).hasSize(1);
	}

	@Test
	public void shouldFindyByVarselbestillingId() {
		varselbestillingRepo.saveAndFlush(createVarselbestilling());

		assertThat(varselbestillingRepo.findByVarselbestillingId(VARSELBESTILLING_ID)).isNotNull();
	}

	@Test
	public void shouldFindyByVarselbestillingIdWhenNoVarsel() {
		Varselbestilling varselbestilling = createVarselbestilling();
		varselbestilling.getVarsels().clear();
		varselbestillingRepo.saveAndFlush(varselbestilling);

		assertThat(varselbestillingRepo.findByVarselbestillingId(VARSELBESTILLING_ID)).isNotNull();
	}

	@Test
	public void shouldFindOneEager() {
		varselbestillingRepo.saveAndFlush(createVarselbestilling());

		Varselbestilling varselbestilling = varselbestillingRepo.findByVarselbestillingIdEager(VARSELBESTILLING_ID);

		assertThat(varselbestilling)
				.isNotNull()
				.extracting(Varselbestilling::getVarsels)
				.isNotNull();
	}

	@Test
	public void shouldFindOneEagerWhenNoVarsel() {
		Varselbestilling varselbestilling = createVarselbestilling();
		varselbestilling.getVarsels().clear();
		varselbestillingRepo.saveAndFlush(varselbestilling);

		varselbestilling = varselbestillingRepo.findByVarselbestillingIdEager(VARSELBESTILLING_ID);

		assertThat(varselbestilling)
				.isNotNull()
				.extracting(Varselbestilling::getVarsels)
				.isNotNull();
	}

	@Test
	public void shouldFindAllEager() {
		varselbestillingRepo.saveAndFlush(createVarselbestilling());

		List<Varselbestilling> allEager = varselbestillingRepo.findAllEager();

		assertThat(allEager)
				.singleElement()
				.extracting(Varselbestilling::getVarsels)
				.isNotNull();
	}

	@Test
	public void shouldFindAllEagerWhenNoVarsel() {
		Varselbestilling varselbestilling = createVarselbestilling();
		varselbestilling.getVarsels().clear();
		varselbestillingRepo.saveAndFlush(varselbestilling);

		List<Varselbestilling> allEager = varselbestillingRepo.findAllEager();

		assertThat(allEager)
				.singleElement()
				.extracting(Varselbestilling::getVarsels)
				.isNotNull();
	}
}