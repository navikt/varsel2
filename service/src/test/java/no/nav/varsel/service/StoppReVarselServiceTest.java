package no.nav.varsel.service;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.repo.VarselbestillingRepo;
import no.nav.varsel.service.support.exception.VarselbestillingNotExistException;
import no.nav.varsel.service.tvarsel004.to.StoppReVarselTo;
import no.nav.varsel.service.tvarsel004.to.StoppReVarselToTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit tests for {@link StoppReVarselService}
 * @author Hiep Luong Nguyen, Computas
 */
@RunWith(MockitoJUnitRunner.class)
public class StoppReVarselServiceTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    VarselbestillingRepo varselbestillingRepo;

    @InjectMocks
    StoppReVarselService stoppReVarselService;

    @Test
    public void shouldThrowExceptionIfVarselbestillingIdNotExist() throws Exception {
        StoppReVarselTo stoppReVarselTo = StoppReVarselToTest.createTo();
        when(varselbestillingRepo.findByVarselbestillingId(stoppReVarselTo.getVarselbestillingId())).thenReturn(null);
        expectedException.expect(VarselbestillingNotExistException.class);
        expectedException.expectMessage("Varselbestilling with varselbestillingId=" + stoppReVarselTo.getVarselbestillingId() + " does not exist");

        stoppReVarselService.behandleVarselbestilling(stoppReVarselTo);
    }

    @Test
    public void shouldUpdateVarselbestilling() throws Exception {
        StoppReVarselTo to = StoppReVarselToTest.createTo();
        Varselbestilling varselbestilling = createVarselbestilling(to.getVarselbestillingId());
        when(varselbestillingRepo.findByVarselbestillingId(to.getVarselbestillingId())).thenReturn(varselbestilling);

        stoppReVarselService.behandleVarselbestilling(to);

        assertThat(varselbestilling.getAntallRevarslinger(), equalTo(0));
        assertThat(varselbestilling.getNesteVarslingDato(), equalTo(null));
    }

    private Varselbestilling createVarselbestilling(String id) {
        Varselbestilling varselbestilling = new Varselbestilling();
        varselbestilling.setVarselbestillingId(id);

        return varselbestilling;
    }
}
