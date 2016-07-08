package no.nav.varsel.batch.bvarsel001;

import static no.nav.varsel.jms.producer.varselbestilling.to.VarselbestillingTo.VarselbestillingToBuilder.aVarselbestillingTo;

import com.google.common.collect.Maps;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.jms.producer.varselbestilling.to.VarselbestillingTo;
import org.springframework.batch.item.ItemProcessor;

/**
 * Processor that maps varselbestilling, enqueues and updates status
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class BestillReVarselMapper implements ItemProcessor<Varselbestilling, VarselbestillingTo> {

	@Override
	public VarselbestillingTo process(Varselbestilling item) throws Exception {
		return aVarselbestillingTo()
				.varselbestillingId(item.getVarselbestillingId())
				.varseltypeId(item.getVarseltypeId())
				.mottakerFnr(item.getFnr())
				.revarsel(true)
				.parameters(Maps.newHashMap(item.getFletteParametere()))
				.build();
	}

}
