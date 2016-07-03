package no.nav.varsel.provider.map;

import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Varselbestilling;
import no.nav.varsel.service.tvarsel005.to.VarselbestillingTo;

/**
 * @author Lars Aune
 */
public interface VarselbestillingMapper {
	Varselbestilling map(VarselbestillingTo varselbestillingTo);
}
