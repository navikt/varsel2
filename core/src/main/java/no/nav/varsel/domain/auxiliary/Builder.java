package no.nav.varsel.domain.auxiliary;

import no.nav.varsel.domain.Constants;
import org.slf4j.MDC;

import javax.persistence.EntityManager;


public abstract class Builder<T extends AbstractDomainObject> {

	protected abstract T build();

	public T buildAndPersist(EntityManager entityManager) {
		if (MDC.get(Constants.USER_ID) == null) {
			MDC.put(Constants.USER_ID, "builderUserId");
		}

		T objectToPersist = build();
		entityManager.persist(objectToPersist);
		entityManager.flush();
		// refresh from db to apply annotations, such as @OrderBy
		entityManager.refresh(objectToPersist);
		return objectToPersist;
	}
}
