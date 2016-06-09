package no.nav.varsel.jms.consumer.tvarsel003;

import static no.nav.varsel.domain.utility.XmlGregorianConverter.toXmlGregorianCalendar;
import static no.nav.varsel.jms.consumer.tvarsel003.support.BestillVarselMapperTest.KEY;
import static no.nav.varsel.jms.consumer.tvarsel003.support.BestillVarselMapperTest.UTLOEPS_TIDSPUNKT;
import static no.nav.varsel.jms.consumer.tvarsel003.support.BestillVarselMapperTest.VAL;
import static no.nav.varsel.jms.consumer.tvarsel003.support.BestillVarselMapperTest.VARSELBESTILLING_ID;
import static no.nav.varsel.jms.consumer.tvarsel003.support.BestillVarselMapperTest.VARSLINGSTYPE;
import static no.nav.varsel.mock.AktoerV2Mock.AKTOER_ID;
import static no.nav.varsel.test.TestUtils.aboutNow;
import static no.nav.varsel.wsconsumer.dkif.support.HentDigitalKontaktinformasjonMapperTest.EPOSTADRESSE;
import static no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumerTest.ANTALL_REVARSLING;
import static no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumerTest.FOERSTE_GANG_TEKST;
import static no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumerTest.PREFERERT_KANAL;
import static no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumerTest.REVARSLING_INTERVALL;
import static no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumerTest.REVARSLING_TEKST;
import static no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumerTest.VARSEL_TITTEL;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.ObjectFactory;
import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.VarselMedHandling;
import no.nav.melding.virksomhet.varselutsending.v2.varselutsending.Person;
import no.nav.melding.virksomhet.varselutsending.v2.varselutsending.Varselutsending;
import no.nav.varsel.domain.code.StatusCode;
import no.nav.varsel.domain.object.Varsel;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.jms.consumer.AbstractConsumerJmsTest;
import no.nav.varsel.jms.consumer.tvarsel003.support.BestillVarselMapperTest;
import no.nav.varsel.jms.producer.VarselutsendingProducer;
import no.nav.varsel.jms.to.xml.JmsReply;
import org.junit.Test;

import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.Queue;
import javax.xml.bind.JAXBElement;
import java.time.LocalDate;
import java.util.Iterator;

/**
 * Itest for TVARSEL003 BestillVarsel
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class BestillVarselConsumerTest extends AbstractConsumerJmsTest {

	@Inject
	private Queue bestillVarselQueue;
	@Inject
	private Queue varselutsendingQueue;

	@Test
	public void shouldBestillFoerstegangVarsel() throws Exception {
		JmsReply jmsReply = sendMessage(bestillVarselQueue, createVarselBestilling(false));
		isOk(jmsReply);

		Varselbestilling varselbestilling = varselbestillingRepo.findByVarselbestillingId(VARSELBESTILLING_ID);

		assertDatabaseFoerstegangVarsel(varselbestilling);
		assertVarselutsending(varselbestilling, varselbestilling.getVarsels().iterator().next());
	}

	@Test
	public void shouldBestillRevarsel() throws Exception {
		// setup
		sendMessage(bestillVarselQueue, createVarselBestilling(false));
		receive(varselutsendingQueue);
		String varselIdFoersteVarsel = varselbestillingRepo.findByVarselbestillingId(VARSELBESTILLING_ID)
				.getVarsels().iterator().next().getVarselId();

		// run
		JmsReply jmsReply = sendMessage(bestillVarselQueue, createVarselBestilling(true));
		isOk(jmsReply);

		Varselbestilling varselbestilling = varselbestillingRepo.findByVarselbestillingId(VARSELBESTILLING_ID);
		Varsel varsel = assertDatabaseRevarsel(varselIdFoersteVarsel, varselbestilling);

		assertVarselutsending(varselbestilling, varsel);
	}

	@Test
	public void shouldNotBackoutForestegangVarselbestillingExists() throws Exception {
		sendMessage(bestillVarselQueue, createVarselBestilling(false));
		receive(varselutsendingQueue);

		// run
		JmsReply jmsReply = sendMessage(bestillVarselQueue, createVarselBestilling(false));
		isOk(jmsReply);
	}

	@Test
	public void shouldNotBackoutReVarselVarselbestillingNotExists() throws Exception {
		// run
		JmsReply jmsReply = sendMessage(bestillVarselQueue, createVarselBestilling(true));
		isOk(jmsReply);
		assertThat(varselbestillingRepo.count(), is(0L));
	}

	@Test
	public void shouldBackoutOnTechnicalErrorAndRollback() throws Exception {
		JAXBElement<VarselMedHandling> varselBestilling = createVarselBestilling(false);
		varselBestilling.getValue().getVarslingstype().setValue(VarselutsendingProducer.FEIL_MQ_UT);
		Message message = sendMessageListenBoq(bestillVarselQueue, varselBestilling);
		isOk(message);
		assertThat(varselbestillingRepo.count(), is(0L));
	}

	protected Varsel assertDatabaseFoerstegangVarsel(Varselbestilling varselbestilling) {
		assertThat(varselbestilling, notNullValue());
		assertThat(varselbestilling.getVarselbestillingId(), is(VARSELBESTILLING_ID));
		assertThat(varselbestilling.getVarslingstype(), is(VARSLINGSTYPE));
		assertThat(varselbestilling.getPreferertKanal(), contains(PREFERERT_KANAL));
		assertThat(varselbestilling.getUtlopTidspunkt(), is(UTLOEPS_TIDSPUNKT));
		assertThat(varselbestilling.getFnr(), is(BestillVarselMapperTest.PERSON_IDENT));
		assertThat(varselbestilling.getAktorId(), is(AKTOER_ID));
		assertThat(varselbestilling.getBestillingTidspunkt(), aboutNow());
		assertThat(varselbestilling.getRevarslingIntervall(), is(REVARSLING_INTERVALL));
		assertThat(varselbestilling.getAntallRevarslinger(), is(ANTALL_REVARSLING));
		assertThat(varselbestilling.getNesteVarslingstidspunkt(), is(LocalDate.now().plusDays(REVARSLING_INTERVALL)));

		assertThat(varselbestilling.getVarsels(), hasSize(1));
		Varsel varsel = varselbestilling.getVarsels().iterator().next();

		assertThat(varsel.getVarselId(), notNullValue());
		assertThat(varsel.getKanal(), is(PREFERERT_KANAL));
		assertThat(varsel.getSendtTidspunkt(), aboutNow());
		assertThat(varsel.getDistribusjonTidspunkt(), nullValue());
		assertThat(varsel.getKontaktInfo(), is(EPOSTADRESSE));
		assertThat(varsel.getStatus(), is(StatusCode.SENDT));
		assertThat(varsel.getFeilbeskrivelse(), nullValue());
		assertThat(varsel.getVarselTittel(), is(VARSEL_TITTEL));
		assertThat(varsel.getVarselTekst(), is(FOERSTE_GANG_TEKST.replace(":" + KEY, VAL)));
		assertThat(varsel.getVarselUrl(), nullValue());
		assertThat(varsel.getErRevarsel(), is(false));
		return varsel;
	}

	protected Varsel assertDatabaseRevarsel(String varselIdFoersteVarsel, Varselbestilling varselbestilling) {
		assertThat(varselbestilling, notNullValue());
		assertThat(varselbestilling.getVarselbestillingId(), is(VARSELBESTILLING_ID));
		assertThat(varselbestilling.getVarslingstype(), is(VARSLINGSTYPE));
		assertThat(varselbestilling.getPreferertKanal(), contains(PREFERERT_KANAL));
		assertThat(varselbestilling.getUtlopTidspunkt(), is(UTLOEPS_TIDSPUNKT));
		assertThat(varselbestilling.getFnr(), is(BestillVarselMapperTest.PERSON_IDENT));
		assertThat(varselbestilling.getAktorId(), is(AKTOER_ID));
		assertThat(varselbestilling.getBestillingTidspunkt(), aboutNow());
		assertThat(varselbestilling.getRevarslingIntervall(), is(REVARSLING_INTERVALL));
		assertThat(varselbestilling.getAntallRevarslinger(), is(ANTALL_REVARSLING));
		assertThat(varselbestilling.getNesteVarslingstidspunkt(), is(LocalDate.now().plusDays(REVARSLING_INTERVALL)));

		assertThat(varselbestilling.getVarsels(), hasSize(2));
		Iterator<Varsel> iterator = varselbestilling.getVarsels().iterator();
		Varsel varsel1 = iterator.next();
		Varsel varsel2 = iterator.next();
		Varsel varsel = varsel1.getVarselId().equals(varselIdFoersteVarsel) ? varsel2 : varsel1;

		assertThat(varsel.getVarselId(), notNullValue());
		assertThat(varsel.getKanal(), is(PREFERERT_KANAL));
		assertThat(varsel.getSendtTidspunkt(), aboutNow());
		assertThat(varsel.getDistribusjonTidspunkt(), nullValue());
		assertThat(varsel.getKontaktInfo(), is(EPOSTADRESSE));
		assertThat(varsel.getStatus(), is(StatusCode.SENDT));
		assertThat(varsel.getFeilbeskrivelse(), nullValue());
		assertThat(varsel.getVarselTittel(), is(VARSEL_TITTEL));
		assertThat(varsel.getVarselTekst(), is(REVARSLING_TEKST.replace(":" + KEY, VAL)));
		assertThat(varsel.getVarselUrl(), nullValue());
		assertThat(varsel.getErRevarsel(), is(true));
		return varsel;
	}

	protected void assertVarselutsending(Varselbestilling varselbestilling, Varsel varsel) {
		Varselutsending varselutsending = receive(varselutsendingQueue);

		assertThat(varselutsending.getVarselId(), is(varsel.getVarselId()));
		assertThat(((Person) varselutsending.getMottaker()).getIdent().getIdent(), is(varselbestilling.getFnr()));
		assertThat(((Person) varselutsending.getMottaker()).getIdent().getType().getValue(), is("FNR"));
		assertThat(varselutsending.getUtloepstidspunkt(), is(toXmlGregorianCalendar(varselbestilling.getUtlopTidspunkt())));
		assertThat(varselutsending.getUtsendelsestidspunkt(), nullValue());
		assertThat(varselutsending.getDistribusjon().getKanal().getValue(), is(varsel.getKanal().getKommunikasjonskanal()));
		assertThat(varselutsending.getDistribusjon().getKontaktinformasjon(), is(varsel.getKontaktInfo()));
		assertThat(varselutsending.getVarslingstype().getValue(), is(varselbestilling.getVarslingstype()));
		assertThat(varselutsending.getVarselTittel(), is(varsel.getVarselTittel()));
		assertThat(varselutsending.getVarselTekst(), is(varsel.getVarselTekst()));
		assertThat(varselutsending.getVarselURL(), nullValue());
	}

	private JAXBElement<VarselMedHandling> createVarselBestilling(boolean revarsel) {
		VarselMedHandling varselBestilling = BestillVarselMapperTest.createVarselBestilling();
		varselBestilling.setRevarsling(revarsel);
		return new ObjectFactory().createVarselMedHandling(varselBestilling);
	}
}