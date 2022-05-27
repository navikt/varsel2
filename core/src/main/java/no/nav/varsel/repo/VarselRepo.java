package no.nav.varsel.repo;

import no.nav.varsel.domain.object.Varsel;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link Varsel}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public interface VarselRepo extends JpaRepository<Varsel, Long> {

	/**
	 * Find {@link Varsel} by varselId
	 *
	 * @param id the varselId
	 * @return the {@link Varsel} or null if not found
	 */
	Varsel findByVarselId(String id);

}
