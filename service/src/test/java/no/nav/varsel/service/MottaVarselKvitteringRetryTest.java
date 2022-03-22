package no.nav.varsel.service;

import no.nav.varsel.domain.code.StatusCode;
import no.nav.varsel.domain.object.Varsel;
import no.nav.varsel.repo.VarselRepo;
import no.nav.varsel.service.tvarsel002.to.MottaVarselKvitteringTo;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Ugur Alpay Cenar, Visma Consulting.
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {MottaVarselKvitteringService.class, MottaVarselKvitteringRetryTest.Config.class})
@ActiveProfiles({"itest", "local"})
public class MottaVarselKvitteringRetryTest {

	@Autowired
	MottaVarselKvitteringService mottaVarselKvitteringService;

	@Autowired
	VarselRepo varselRepo;

	@EnableRetry
	@Configuration
	public static class Config {

		@Bean
		public VarselRepo varselRepo() {
			return mock(VarselRepo.class);
		}
	}

	@After
	public void resetMocks() {
		Mockito.reset(varselRepo);
	}

	@Test
	public void shouldRetry() {
		MottaVarselKvitteringTo mottaVarselKvitteringTo = new MottaVarselKvitteringTo();
		mottaVarselKvitteringTo.setVarselId("1");
		Varsel varsel = new Varsel();
		varsel.setStatus(StatusCode.SENDT);
		when(varselRepo.findByVarselId(anyString())).thenReturn(null).thenReturn(varsel);
		mottaVarselKvitteringService.behandleKvitteringsmelding(mottaVarselKvitteringTo);
		assertThat(varsel.getKvitteringTidspunkt(), notNullValue());
		verify(varselRepo, times(2)).findByVarselId(anyString());
	}
}
