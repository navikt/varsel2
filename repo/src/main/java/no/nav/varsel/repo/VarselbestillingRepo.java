package no.nav.varsel.repo;

import no.nav.varsel.domain.object.Varselbestilling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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

	/**
	 * Find {@link Varselbestilling} by varselbestillingId and eagerly fetch varsels
	 *
	 * @param id the varselbestillingId
	 * @return the {@link Varselbestilling} or null if not found
	 */
	@Query("select vb from Varselbestilling vb join fetch vb.varsels where vb.varselbestillingId = (:id)")
	Varselbestilling findByVarselbestillingIdEager(@Param("id") String id);

	/**
	 * Find all {@link Varselbestilling} and eagerly fetch varsels
	 *
	 * @return the a list of {@link Varselbestilling}
	 */
	@Query("select vb from Varselbestilling vb join fetch vb.varsels")
	List<Varselbestilling> findAllEager();
}
