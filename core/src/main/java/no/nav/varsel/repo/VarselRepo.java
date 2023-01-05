package no.nav.varsel.repo;

import no.nav.varsel.domain.object.Varsel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VarselRepo extends JpaRepository<Varsel, Long> {

	/**
	 * Find {@link Varsel} by varselId
	 *
	 * @param id the varselId
	 * @return the {@link Varsel} or null if not found
	 */
	Varsel findByVarselId(String id);

}
