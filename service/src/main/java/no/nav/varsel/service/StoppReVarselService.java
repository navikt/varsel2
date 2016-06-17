package no.nav.varsel.service;

import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.repo.VarselbestillingRepo;
import no.nav.varsel.service.support.exception.VarselbestillingNotExistException;
import no.nav.varsel.service.tvarsel004.to.StoppReVarselTo;

import javax.inject.Inject;

/**
 * Service for StoppReVarsel
 * @author Hiep Luong Nguyen, Computas
 */
public class StoppReVarselService {
    @Inject
    private VarselbestillingRepo varselbestillingRepo;

    public void behandleVarselbestilling(StoppReVarselTo stoppReVarselTo) {
        Varselbestilling varselbestilling = findVarselbestilling(stoppReVarselTo);
        updateVarselbestilling(varselbestilling);
    }

    private Varselbestilling findVarselbestilling(StoppReVarselTo stoppReVarselTo) {
        Varselbestilling varselbestilling =
                varselbestillingRepo.findByVarselbestillingId(stoppReVarselTo.getVarselbestillingId());
        if (varselbestilling == null) {
            throw new VarselbestillingNotExistException(stoppReVarselTo.getVarselbestillingId());
        }
        return varselbestilling;
    }

    private void updateVarselbestilling(Varselbestilling varselbestilling) {
        varselbestilling.setAntallRevarslinger(0);
        varselbestilling.setNesteVarslingDato(null);
    }
}
