package no.nav.varsel.provider.map;

import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Varsel;
import no.nav.varsel.service.tvarsel005.to.VarselTo;

/**
 * @author Lars Aune
 */
public interface VarselMapper {
	Varsel map(VarselTo varselTo);
}
