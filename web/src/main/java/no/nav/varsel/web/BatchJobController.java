package no.nav.varsel.web;

import no.nav.brevogarkiv.batch.common.provider.launch.support.ModigJobOperator;
import no.nav.brevogarkiv.batch.common.provider.rs.ModigJobController;

import javax.inject.Inject;

/**
 * Controller for spring batch
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class BatchJobController extends ModigJobController {

	@Inject
	private ModigJobOperator modigJobOperator;

	@Override
	public ModigJobOperator getModigJobOperator() {
		return modigJobOperator;
	}
}
