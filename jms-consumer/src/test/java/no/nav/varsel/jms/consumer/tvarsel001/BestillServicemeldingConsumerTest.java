package no.nav.varsel.jms.consumer.tvarsel001;

import static no.nav.varsel.domain.code.KanalCode.EPOST;
import static no.nav.varsel.domain.utility.XmlGregorianConverter.toXmlGregorianCalendar;
import static no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapperTest.MOTTAKER;
import static no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapperTest.UTLOEPSTIDSPUNKT_LDT;
import static no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapperTest.VAL;
import static no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapperTest.VARSELTYPE_ID;
import static no.nav.varsel.jms.producer.VarselutsendingProducer.FEIL_MQ_UT;
import static no.nav.varsel.mock.AktoerV2Mock.PERSON_IDENT;
import static no.nav.varsel.repo.TestdataUtil.FUNKSJONELL_FEIL;
import static no.nav.varsel.repo.TestdataUtil.TEKNISK_FEIL;
import static no.nav.varsel.test.TestUtils.aboutNow;
import static no.nav.varsel.wsconsumer.dkif.support.HentDigitalKontaktinformasjonMapperTest.EPOSTADRESSE;
import static no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumerTest.FOERSTE_GANG_TEKST;
import static no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumerTest.FOERSTE_GANG_TEKST_VARSEL_URL;
import static no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumerTest.VARSEL_TITTEL;
import static no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumerTest.VARSEL_URL;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import no.nav.melding.virksomhet.varsel.v1.varsel.AktoerId;
import no.nav.melding.virksomhet.varsel.v1.varsel.ObjectFactory;
import no.nav.melding.virksomhet.varsel.v1.varsel.Varsel;
import no.nav.melding.virksomhet.varsel.v1.varsel.Varslingstyper;
import no.nav.melding.virksomhet.varselutsending.v2.varselutsending.Varselutsending;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.code.StatusCode;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.jms.consumer.AbstractConsumerJmsTest;
import no.nav.varsel.jms.consumer.JmsConsumer;
import no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapperTest;
import no.nav.varsel.jms.to.xml.JmsReply;
import org.junit.Test;

import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.Queue;
import javax.xml.bind.JAXBElement;
import java.util.UUID;

/**
 * Itest for {@link BestillServicemeldingConsumer}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class BestillServicemeldingConsumerTest extends AbstractConsumerJmsTest {

	@Inject
	private Queue bestillServicemeldingQueue;
	@Inject
	private Queue varselutsendingQueue;

	@Test
	public void shouldReceieveJms() throws Exception {
		JmsReply response = sendMessage(bestillServicemeldingQueue, createVarsel());

		isOk(response);
		assertThat(varselbestillingRepo.count(), is(1L));

		String varselTekst = FOERSTE_GANG_TEKST.replace("{mottaker}", VAL);
		String varselId = assertDb(varselTekst).getVarselId();
		assertVarselutsendingQueue(varselTekst, varselId);
	}

	@Test
	public void shouldWeaveVarselUrl() throws Exception {
		Varsel varsel = BestillServicemeldingMapperTest.createVarsel();
		Varslingstyper varslingstype = new Varslingstyper();
		varslingstype.setValue("varsel_varselUrl");
		varsel.setVarslingstype(varslingstype);
		varsel.getParameterListe().clear();
		JmsReply response = sendMessage(bestillServicemeldingQueue, new ObjectFactory().createVarsel(varsel));

		isOk(response);
		assertThat(varselbestillingRepo.count(), is(1L));

		String varselTekst = FOERSTE_GANG_TEKST_VARSEL_URL.replace("{varselUrl}", VARSEL_URL);
		Varselutsending varselutsending = receive(varselutsendingQueue);
		assertThat(varselutsending.getVarselTekst(), equalTo(varselTekst));
	}

	@Test
	public void shouldWeaveVarselurl() throws Exception {
		Varsel varsel1 = BestillServicemeldingMapperTest.createVarsel();
		varsel1.getParameterListe().clear();


		JAXBElement<Varsel> varsel = createVarsel();
		JmsReply response = sendMessage(bestillServicemeldingQueue, varsel);

		isOk(response);
		assertThat(varselbestillingRepo.count(), is(1L));

		String varselTekst = FOERSTE_GANG_TEKST.replace("{mottaker}", VAL);
		String varselId = assertDb(varselTekst).getVarselId();
		assertVarselutsendingQueue(varselTekst, varselId);
	}

	@Test
	public void shouldPutOnBackoutIfFailedWs() throws Exception {
		JAXBElement<Varsel> varsel = createVarsel();
		((AktoerId) varsel.getValue().getMottaker()).setAktoerId(TEKNISK_FEIL);
		Message response = sendMessageListenBoq(bestillServicemeldingQueue, varsel);

		isOk(response);
	}

	@Test
	public void shouldNotPutOnBackoutIfFailedWsFunksjonell() throws Exception {
		JAXBElement<Varsel> varsel = createVarsel();
		((AktoerId) varsel.getValue().getMottaker()).setAktoerId(FUNKSJONELL_FEIL);
		JmsReply response = sendMessage(bestillServicemeldingQueue, varsel);

		isOk(response);
	}

	@Test
	public void shouldPutOnBackoutAndRollbackIfFailedAfterDbSave() throws Exception {
		JAXBElement<Varsel> varsel = createVarsel();
		varsel.getValue().getVarslingstype().setValue(FEIL_MQ_UT);
		Message response = sendMessageListenBoq(bestillServicemeldingQueue, varsel);

		isOk(response);
		assertThat(varselbestillingRepo.count(), is(0L));

		Object receive = receive(varselutsendingQueue);
		assertThat(receive, nullValue());
	}

	public static JAXBElement<Varsel> createVarsel() {
		return new ObjectFactory().createVarsel(BestillServicemeldingMapperTest.createVarsel());
	}

	private no.nav.varsel.domain.object.Varsel assertDb(String varselTekst) {
		Varselbestilling varselbestilling = varselbestillingRepo.findAllEager().get(0);
		assertThat(UUID.fromString(varselbestilling.getVarselbestillingId()).toString(), is(varselbestilling.getVarselbestillingId()));
		assertThat(varselbestilling.getVarseltypeId(), is(VARSELTYPE_ID));
		assertThat(varselbestilling.getUtlopTidspunkt(), is(equalTo(UTLOEPSTIDSPUNKT_LDT)));
		assertThat(varselbestilling.getFnr(), is(PERSON_IDENT));
		assertThat(varselbestilling.getAktorId(), is(MOTTAKER));
		assertThat(varselbestilling.getBestillingTidspunkt(), aboutNow());
		assertThat(varselbestilling.getRevarslingIntervall(), nullValue());
		assertThat(varselbestilling.getAntallRevarslinger(), nullValue());
		assertThat(varselbestilling.getNesteVarslingDato(), nullValue());
		assertThat(varselbestilling.getChangeStamp().getOpprettetAv(), is(JmsConsumer.BESTILL_SERVICEMELDING.getServiceName()));
		assertThat(varselbestilling.getChangeStamp().getOpprettetDato(), aboutNow());

		assertThat(varselbestilling.getVarsels(), hasSize(1));

		no.nav.varsel.domain.object.Varsel varsel = varselbestilling.getVarsels().iterator().next();
		assertThat(UUID.fromString(varsel.getVarselId()).toString(), is(varsel.getVarselId()));
		assertThat(varsel.getKanal(), is(KanalCode.EPOST));
		assertThat(varsel.getSendtTidspunkt(), aboutNow());
		assertThat(varsel.getDistribusjonTidspunkt(), nullValue());
		assertThat(varsel.getKontaktInfo(), is(EPOSTADRESSE));
		assertThat(varsel.getStatus(), is(StatusCode.SENDT));
		assertThat(varsel.getFeilbeskrivelse(), nullValue());
		assertThat(varsel.getVarselTittel(), is(VARSEL_TITTEL));
		assertThat(varsel.getVarselTekst(), is(varselTekst));
		assertThat(varsel.getVarselUrl(), nullValue());
		assertThat(varsel.getErRevarsel(), is(false));
		assertThat(varsel.getChangeStamp().getOpprettetAv(), is(JmsConsumer.BESTILL_SERVICEMELDING.getServiceName()));
		assertThat(varsel.getChangeStamp().getOpprettetDato(), aboutNow());
		return varsel;
	}

	private void assertVarselutsendingQueue(String varselTekst, String varselId) {
		Varselutsending varselutsending = receive(varselutsendingQueue);

		assertThat(varselutsending.getVarselId(), is(varselId));
		assertThat(((no.nav.melding.virksomhet.varselutsending.v2.varselutsending.AktoerId)
				varselutsending.getMottaker()).getAktoerId(), is(MOTTAKER));
		assertThat(varselutsending.getUtloepstidspunkt(), equalTo(toXmlGregorianCalendar(UTLOEPSTIDSPUNKT_LDT)));
		assertThat(varselutsending.getDistribusjon().getKanal().getValue(), is(EPOST.getKommunikasjonskanal()));
		assertThat(varselutsending.getDistribusjon().getKontaktinformasjon(), is(EPOSTADRESSE));
		assertThat(varselutsending.getVarseltypeId(), is(VARSELTYPE_ID));
		assertThat(varselutsending.getVarselTittel(), is(VARSEL_TITTEL));
		assertThat(varselutsending.getVarselTekst(), is(varselTekst));
		assertThat(varselutsending.getVarselURL(), nullValue());
	}

}