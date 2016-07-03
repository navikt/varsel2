package no.nav.varsel.repo.support;

import no.nav.varsel.domain.code.StatusCode;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.repo.TVARSEL005Repo;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lars Aune
 */
public class TVARSEL005RepoImpl implements TVARSEL005Repo{
	public static final String DATO_FOM_QL_SEGMENT = " and vb.bestillingTidspunkt >= :datoFom ";
	public static final String DATO_TOM_QL_SEGMENT = " and vb.bestillingTidspunkt <= :datoTom ";
	@Inject
	private EntityManager entityManager;

	@Override
	public List<Varselbestilling> findFerdigbehandletVarselbestillinger(String bruker,
																		LocalDateTime datoFom,
																		LocalDateTime datoTom) {
		List<Varselbestilling> result = new ArrayList<>();
		if (bruker == null) {
			return result;
		}
		String jpql = "select vb from Varselbestilling Vb inner join Vb.varsels v" +
				" where vb.aktorId = :bruker " +
				datoFom != null ? DATO_FOM_QL_SEGMENT : "" +
				datoTom != null ? DATO_TOM_QL_SEGMENT : "";
		try {
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

			result.addAll(resultList);
		} catch (NoResultException e) {
			return result;
		}
		return result.stream().filter(vb -> hasAtLeastOneFerdigbehandletVarsel(vb)).collect(Collectors.toList());
	}

	private boolean hasAtLeastOneFerdigbehandletVarsel(Varselbestilling vb) {
		return vb.getVarsels().stream().anyMatch(v -> StatusCode.FERDIGBEHANDLET.equals(v.getStatus()));
	}
}
