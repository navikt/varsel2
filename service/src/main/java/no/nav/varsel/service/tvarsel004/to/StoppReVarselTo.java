package no.nav.varsel.service.tvarsel004.to;

import static no.nav.varsel.service.support.ValueValidator.hasText;

/**
 * To for {@link StoppReVarselTo}
 * @author Hiep Luong Nguyen, Computas
 */
public class StoppReVarselTo {
    private String varselbestillingId;

    public void validateTo() {
        hasText(varselbestillingId, "varselbestillingId");
    }

    public String getVarselbestillingId() {
        return varselbestillingId;
    }

    public void setVarselbestillingId(String varselbestillingId) {
        this.varselbestillingId = varselbestillingId;
    }
}
