package no.nav.varsel.repo.support;

import no.nav.varsel.repo.VarselRepoCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Implementation of custom methods in VarselRepo
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselRepoImpl implements VarselRepoCustom {

	@PersistenceContext(unitName = "primary")
	private EntityManager entityManager;

	@Override
	public void ping() {
		entityManager.createQuery("select count(*) from Varsel where id is null", Long.class).getSingleResult();
	}

}
