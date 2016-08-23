package no.nav.varsel.batch.support;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.internal.util.reflection.Whitebox.getInternalState;

import org.junit.Test;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.transaction.interceptor.TransactionAttribute;

/**
 * Unit test for {@link FaultTolerantStepBuilderLazyTransactionAttribute}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class FaultTolerantStepBuilderLazyTransactionAttributeTest {

	private final StepBuilder parent = new StepBuilder("name");

	private FaultTolerantStepBuilderLazyTransactionAttribute<Object, Object> stepBuilder =
			new FaultTolerantStepBuilderLazyTransactionAttribute<>(new SimpleStepBuilder<>(parent));

	@Test
	public void shouldSetTransactionAttributeLazy() throws Exception {
		TransactionAttribute mock = mock(TransactionAttribute.class);
		stepBuilder.transactionAttribute(mock);

		assertThat(getInternalState(stepBuilder, "transactionAttribute"), is(mock));
		verifyZeroInteractions(mock);
	}

}