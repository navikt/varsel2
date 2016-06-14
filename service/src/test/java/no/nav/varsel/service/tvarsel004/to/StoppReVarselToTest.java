package no.nav.varsel.service.tvarsel004.to;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

/**
 * Unit test for {@link StoppReVarselTo}
 * @author Hiep Luong Nguyen, Computas
 */
@RunWith(MockitoJUnitRunner.class)
public class StoppReVarselToTest {
    public static final String VARSELBESTILLING_ID = UUID.randomUUID().toString();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldValidateTo() throws Exception {
        createTo().validateTo();
    }

    @Test
    public void shouldValidateMissingVarselbestillingId() throws Exception {
        StoppReVarselTo to = createTo();
        to.setVarselbestillingId(null);

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("varselbestillingId cannot be empty or missing");

        to.validateTo();
    }

    @Test
    public void shouldValidateEmptyVarselbestillingId() throws Exception {
        StoppReVarselTo to = createTo();
        to.setVarselbestillingId("");

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("varselbestillingId cannot be empty or missing");

        to.validateTo();
    }

    public static StoppReVarselTo createTo() {
        StoppReVarselTo to = new StoppReVarselTo();
        to.setVarselbestillingId(VARSELBESTILLING_ID);

        return to;
    }
}
