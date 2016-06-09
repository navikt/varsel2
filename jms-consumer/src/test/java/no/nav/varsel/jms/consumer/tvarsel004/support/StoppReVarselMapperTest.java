package no.nav.varsel.jms.consumer.tvarsel004.support;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import no.nav.melding.virksomhet.stopprevarsel.v1.stopprevarsel.StoppReVarsel;
import no.nav.varsel.service.tvarsel004.to.StoppReVarselTo;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.UUID;

/**
 * Unit tests for {@link StoppReVarselMapper}
 * @author Hiep Luong Nguyen, Computas
 */
public class StoppReVarselMapperTest {
    public static final String VARSELBESTILLING_ID = UUID.randomUUID().toString();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private StoppReVarselMapper mapper = new StoppReVarselMapper();

    @Test
    public void shouldMap() throws Exception {
        StoppReVarselTo mappedStoppReVarsel = mapper.map(createStoppReVarsel());

        assertThat(mappedStoppReVarsel.getVarselbestillingId(), equalTo(VARSELBESTILLING_ID));
    }

    public static StoppReVarsel createStoppReVarsel() {
        StoppReVarsel stoppReVarsel = new StoppReVarsel();
        stoppReVarsel.setVarselbestillingId(VARSELBESTILLING_ID);
        return stoppReVarsel;
    }
}
