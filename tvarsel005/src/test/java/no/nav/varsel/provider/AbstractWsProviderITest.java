package no.nav.varsel.provider;

import no.nav.modig.core.context.SubjectHandlerUtils;
import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.varsel.config.WsProviderTestConfig;
import no.nav.varsel.config.alias.ListenerProperties;
import no.nav.varsel.config.alias.MqGatewayProperties;
import no.nav.varsel.repo.VarselRepo;
import no.nav.varsel.repo.VarselbestillingRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ActiveProfiles;

import static no.nav.varsel.domain.Constants.USER_ID;

@SpringBootTest(classes = WsProviderTestConfig.class)
@ActiveProfiles({"itest"})
@EnableConfigurationProperties({ListenerProperties.class, MqGatewayProperties.class})
@AutoConfigureWireMock(port = 0)
public abstract class AbstractWsProviderITest {

	@Autowired
	protected VarselbestillingRepo varselbestillingRepo;

	@Autowired
	protected VarselRepo varselRepo;

	@MockBean
	protected JmsTemplate jmsTemplate;


	@BeforeAll
	public static void setUpStatic() {
		System.setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass", ThreadLocalSubjectHandler.class.getName());
		System.setProperty("no.nav.modig.security.systemuser.username", "no/nav/varsel");
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
