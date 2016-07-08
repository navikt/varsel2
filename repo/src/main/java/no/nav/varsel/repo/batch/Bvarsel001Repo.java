package no.nav.varsel.repo.batch;

import no.nav.varsel.domain.object.worktable.Bvarsel001WorkTable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repo for Bvarsel001 arbeidstabell
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public interface Bvarsel001Repo extends JpaRepository<Bvarsel001WorkTable, String> {
}
