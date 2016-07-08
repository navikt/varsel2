package no.nav.varsel.batch.bvarsel001;

import no.nav.varsel.domain.object.Varselbestilling;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDate;

/**
 * Processor that maps varselbestilling, enqueues and updates status
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class UpdateVarselbestillingProcessor implements ItemProcessor<Varselbestilling, Varselbestilling> {

	@Override
	public Varselbestilling process(Varselbestilling item) throws Exception {
		updateVarselbestilling(item);
		return item;
	}

	private void updateVarselbestilling(Varselbestilling item) {
		Integer nyAntallRevarslinger = item.getAntallRevarslinger() - 1;
		if (nyAntallRevarslinger <= 0) {
			item.setAntallRevarslinger(null);
			item.setNesteVarslingDato(null);
		} else {
			item.setAntallRevarslinger(nyAntallRevarslinger);
			item.setNesteVarslingDato(LocalDate.now().plusDays(item.getRevarslingIntervall()));
		}
	}
}
