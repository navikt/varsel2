package no.nav.varsel.domain.auxiliary;

import no.nav.varsel.domain.Constants;
import no.nav.varsel.domain.object.Varselbestilling;
import org.hibernate.type.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChangeStampInterceptorTest {

	private static final String USER = "test";

	@Mock
	private Type changeStampType;

	Varselbestilling entity = new Varselbestilling(1L, 1L);

	private ChangeStampInterceptor interceptor = new ChangeStampInterceptor();

	@BeforeEach
	public void setUp() {
		when(changeStampType.getReturnedClass()).thenAnswer(invocationOnMock -> ChangeStamp.class);
		MDC.put(Constants.USER_ID, USER);
	}

	@Test
	public void shouldCreateChangeStampOnSave() throws Exception {
		Object[] state = new Object[1];
		interceptor.onSave(entity, null, state, null, new Type[]{changeStampType});

		ChangeStamp changeStamp = (ChangeStamp) state[0];
		assertThat(changeStamp.getOpprettetAv(), is(USER));
		assertThat(changeStamp.getOpprettetDato(), is(notNullValue()));
	}

	@Test
	public void shouldUpdateChangeStampOnUpdate() throws Exception {
		ChangeStamp changeStamp = new ChangeStamp("Other user");
		Object[] currentState = new Object[]{changeStamp};

		interceptor.onFlushDirty(entity, null, currentState, null, null, new Type[]{changeStampType});

		assertThat(changeStamp.getEndretAv(), is(USER));
		assertThat(changeStamp.getEndretDato(), is(notNullValue()));
	}

}
