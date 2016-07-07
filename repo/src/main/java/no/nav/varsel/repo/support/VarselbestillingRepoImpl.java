package no.nav.varsel.repo.support;

import no.nav.varsel.domain.code.StatusCode;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.repo.VarselbestillingRepoCustom;
import org.springframework.util.Assert;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lars Aune
 */
public class VarselbestillingRepoImpl implements VarselbestillingRepoCustom {

	@Inject
	private EntityManager entityManager;

	@Override
	public List<Varselbestilling> findFerdigbehandletVarselbestillinger(String bruker,
																		LocalDateTime datoFom,
																		LocalDateTime datoTom) {
		Assert.notNull(bruker, "bruker is null");

		String jpql = "select distinct vb from Varselbestilling vb join fetch vb.varsels v" +
				" where " +
				"(vb.aktorId = :bruker or vb.fnr = :bruker) " +
				(datoFom != null ? " and vb.bestillingTidspunkt >= :datoFom" : "") +
				(datoTom != null ? " and vb.bestillingTidspunkt <= :datoTom" : "");

		TypedQuery<Varselbestilling> query = entityManager
				.createQuery(jpql, Varselbestilling.class)
				.setParameter("bruker", bruker);

		if (datoFom != null) {
			query.setParameter("datoFom", datoFom);
		}
		if (datoTom != null) {
			query.setParameter("datoTom", datoTom);
		}

		List<Varselbestilling> resultList = query.getResultList();

		return resultList.stream().filter(this::hasAtLeastOneFerdigbehandletVarsel).collect(Collectors.toList());
	}

	private boolean hasAtLeastOneFerdigbehandletVarsel(Varselbestilling vb) {
		return vb.getVarsels().stream().anyMatch(v -> StatusCode.FERDIGBEHANDLET.equals(v.getStatus()));
	}
}
