package no.nav.varsel.repo;

import no.nav.varsel.domain.object.Varselbestilling;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link Varselbestilling}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public interface VarselbestillingRepo extends JpaRepository<Varselbestilling, Long>, VarselbestillingRepoCustom {

	/**
	 * Find {@link Varselbestilling} by varselbestillingId
	 *
	 * @param id the varselbestillingId
	 * @return the {@link Varselbestilling} or null if not found
	 */
	Varselbestilling findByVarselbestillingId(String id);
}
