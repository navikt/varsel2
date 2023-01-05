package no.nav.varsel.repo;

import no.nav.varsel.domain.object.Varselbestilling;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.time.LocalDateTime;
import java.util.List;

public interface VarselbestillingRepoCustom {

	/**
	 * Find (@Link Varselbestilling) by bruker, datoFom and datoTom
	 *
	 * @param bruker fnr or aktoerId
	 * @param datoFom date from inclusive, can be null
	 * @param datoTom date to inclusive, can be null
	 * @return the set of (@Link Varselbestilling) satisfying the input parameters. The set is empty if no Varselbestilling
	 * satisfies the input parameters. In addition to satisfying the input parameters, the OrgNr property of the
	 * (@Link Varselbestilling) has to be null and the (@Link Varselbestilling) has to have at least one (@Link Varsel)
	 * where it's Status property has the value Ferdigbehandlet for the (@Link Varselbestilling) to be included in the returned
	 * set.
	 * @throws InvalidDataAccessApiUsageException if bruker is null or empty
	 */
	List<Varselbestilling> findFerdigbehandletVarselbestillinger(String bruker, LocalDateTime datoFom, LocalDateTime datoTom);
}
