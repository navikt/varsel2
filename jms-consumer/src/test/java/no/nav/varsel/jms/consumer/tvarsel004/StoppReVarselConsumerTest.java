package no.nav.varsel.jms.consumer.tvarsel004;

import static no.nav.varsel.jms.consumer.tvarsel004.StoppReVarselConsumer.TVARSEL004;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import no.nav.melding.virksomhet.stopprevarsel.v1.stopprevarsel.ObjectFactory;
import no.nav.melding.virksomhet.stopprevarsel.v1.stopprevarsel.StoppReVarsel;
import no.nav.melding.virksomhet.varsel.v1.varsel.Varsel;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.jms.consumer.AbstractConsumerJmsTest;
import no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapperTest;
import no.nav.varsel.jms.consumer.tvarsel004.support.StoppReVarselMapperTest;
import no.nav.varsel.jms.to.xml.JmsReply;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.inject.Inject;
import javax.jms.Queue;
import javax.xml.bind.JAXBElement;
import java.time.LocalDate;
import java.util.List;

/**
 * Itest for {@link StoppReVarselConsumer}
 * @author Hiep Luong Nguyen, Computas
 */
public class StoppReVarselConsumerTest extends AbstractConsumerJmsTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Inject
    private Queue revarselStoppQueue;
    @Inject
    private Queue bestillServicemeldingQueue;

    @Test
    public void shouldPersistStoppReVarselMessage() throws Exception {
        Varselbestilling varselbestilling = persistVarselbestilling();
        JAXBElement<StoppReVarsel> stoppReVarsel = createStoppReVarselJaxBElement(varselbestilling.getVarselbestillingId());
        JmsReply response = sendMessage(revarselStoppQueue, stoppReVarsel);
        assertThat(response.isOk(), is(true));

        Varselbestilling processedVarselbestilling = varselbestillingRepo.findByVarselbestillingId(varselbestilling.getVarselbestillingId());
        assertThat(processedVarselbestilling.getAntallRevarslinger(), equalTo(0));
        assertThat(processedVarselbestilling.getNesteVarslingDato(), is(nullValue()));
        assertThat(processedVarselbestilling.getChangeStamp().getEndretAv(), is(TVARSEL004));
    }

    public static JAXBElement<StoppReVarsel> createStoppReVarselJaxBElement(String varselbestillingId) {
        return new ObjectFactory().createStoppReVarsel(createStoppReVarsel(varselbestillingId));
    }

    private static StoppReVarsel createStoppReVarsel(String varselbestillingId) {
        StoppReVarsel stoppReVarsel = StoppReVarselMapperTest.createStoppReVarsel();
        stoppReVarsel.setVarselbestillingId(varselbestillingId);

        return stoppReVarsel;
    }

    private Varselbestilling persistVarselbestilling() {
        JmsReply createVarselresponse = sendMessage(bestillServicemeldingQueue, createVarsel());
        assertTrue(createVarselresponse != null && createVarselresponse.isOk());
        List<Varselbestilling> varselbestillings = varselbestillingRepo.findAllEager();
        assertThat(varselbestillings, hasSize(1));
        Varselbestilling varselbestilling = varselbestillings.iterator().next();
        assertThat(varselbestilling.getVarsels(), hasSize(1));
        varselbestilling.setAntallRevarslinger(5);
        varselbestilling.setNesteVarslingDato(LocalDate.now());
        return varselbestilling;
    }

    public static JAXBElement<Varsel> createVarsel() {
        return new no.nav.melding.virksomhet.varsel.v1.varsel.ObjectFactory().createVarsel(BestillServicemeldingMapperTest.createVarsel());
    }
}
