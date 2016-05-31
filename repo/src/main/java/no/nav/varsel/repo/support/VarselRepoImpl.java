package no.nav.varsel.repo.support;

import no.nav.varsel.repo.VarselRepoCustom;

import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 * Implementation of custom methods in VarselRepo
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselRepoImpl implements VarselRepoCustom {

	@Inject
	private EntityManager entityManager;

	@Override
	public void ping() {
		entityManager.createQuery("select count(*) from Varsel where id is null", Long.class).getSingleResult();
	}

}
