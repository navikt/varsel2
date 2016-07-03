package no.nav.varsel.repo;

import no.nav.varsel.domain.object.Varselbestilling;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Lars Aune
 */
public interface TVARSEL005Repo {

	/**
	 * Find (@Link Varselbestilling) by bruker, datoFom and datoTom
	 * @param bruker fnr or aktoerId
	 * @param datoFom
	 * @param datoTom
	 * @return the set of (@Link Varselbestilling) satisfying the input parameters. The set is empty if no Varselbestilling
	 * satisfies the input parameters.
	 */
	List<Varselbestilling> findFerdigbehandletVarselbestillinger(String bruker, LocalDateTime datoFom, LocalDateTime datoTom);
}
