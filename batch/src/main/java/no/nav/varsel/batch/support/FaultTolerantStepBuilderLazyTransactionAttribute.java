package no.nav.varsel.batch.support;

import static org.springframework.util.ReflectionUtils.findField;

import org.springframework.batch.core.step.builder.AbstractTaskletStepBuilder;
import org.springframework.batch.core.step.builder.FaultTolerantStepBuilder;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.transaction.interceptor.TransactionAttribute;

import java.lang.reflect.Field;

/**
 * Fault tolerant stepbuilder that can set a lazy bean proxy (step/jobscope) transactionattribute
 *
 * @author Andreas Skomedal, Visma Consulting.
 * @see org.springframework.batch.core.step.builder.FaultTolerantStepBuilder
 */
public class FaultTolerantStepBuilderLazyTransactionAttribute<I, O> extends FaultTolerantStepBuilder<I, O> {
	public FaultTolerantStepBuilderLazyTransactionAttribute(SimpleStepBuilder<I, O> parent) {
		super(parent);
	}

	/**
	 * Set {@link TransactionAttribute}, can be a proxy
	 *
	 * @param transactionAttribute the {@link TransactionAttribute}
	 * @return this builder
	 */
	@Override
	public AbstractTaskletStepBuilder<SimpleStepBuilder<I, O>> transactionAttribute(TransactionAttribute transactionAttribute) {
		Field field = findField(FaultTolerantStepBuilderLazyTransactionAttribute.class, "transactionAttribute");
		field.setAccessible(true);
		try {
			field.set(this, transactionAttribute);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		return this;
	}
}
