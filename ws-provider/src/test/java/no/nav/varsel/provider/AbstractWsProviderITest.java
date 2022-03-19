package no.nav.varsel.provider;

import no.nav.modig.core.context.SubjectHandlerUtils;
import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.modig.testcertificates.TestCertificates;
import no.nav.varsel.config.JmsTestConfig;
import no.nav.varsel.config.WsProviderTestConfig;
import no.nav.varsel.repo.VarselRepo;
import no.nav.varsel.repo.VarselbestillingRepo;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.slf4j.MDC;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static no.nav.varsel.domain.Constants.USER_ID;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = WsProviderTestConfig.class)
@ActiveProfiles({"itest", "local"})
@AutoConfigureWireMock(port = 0)
public abstract class AbstractWsProviderITest {

	@Inject
	protected VarselbestillingRepo varselbestillingRepo;
	
	@Inject
	protected VarselRepo varselRepo;


	@BeforeClass
	public static void setUpStatic() throws Exception {
		System.setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());
		System.setProperty("no.nav.modig.security.systemuser.username", "varsel");
		System.setProperty("no.nav.modig.security.systemuser.password", "passord");
		TestCertificates.setupKeyAndTrustStore();
		SubjectHandlerUtils.setInternBruker(USER_ID);
	}

	@Before
	public void setUpAbstract() {
		varselbestillingRepo.deleteAll();
		MDC.put(USER_ID, "wsprovitest");
	}

	@After
	public void tearDownAbstract() {
		varselbestillingRepo.deleteAll();
		MDC.remove(USER_ID);
	}
}
