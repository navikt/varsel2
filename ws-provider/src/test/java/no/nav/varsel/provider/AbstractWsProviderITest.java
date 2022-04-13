package no.nav.varsel.provider;

import no.nav.modig.core.context.SubjectHandlerUtils;
import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.varsel.config.WsProviderTestConfig;
import no.nav.varsel.repo.VarselRepo;
import no.nav.varsel.repo.VarselbestillingRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static no.nav.varsel.domain.Constants.USER_ID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WsProviderTestConfig.class)
@ActiveProfiles({"itest", "local"})
@AutoConfigureWireMock(port = 0)
public abstract class AbstractWsProviderITest {

	@Autowired
	protected VarselbestillingRepo varselbestillingRepo;
	
	@Autowired
	protected VarselRepo varselRepo;


	@BeforeAll
	public static void setUpStatic() {
		System.setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());
		System.setProperty("no.nav.modig.security.systemuser.username", "varsel");
		System.setProperty("no.nav.modig.security.systemuser.password", "passord");
		SubjectHandlerUtils.setInternBruker(USER_ID);
	}

	@BeforeEach
	public void setUpAbstract() {
		varselbestillingRepo.deleteAll();
		MDC.put(USER_ID, "wsprovitest");
	}

	@AfterEach
	public void tearDownAbstract() {
		varselbestillingRepo.deleteAll();
		MDC.remove(USER_ID);
	}
}
