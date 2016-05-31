package no.nav.varsel.jms.consumer.tvarsel001;

import static no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapperTest.MOTTAKER;
import static no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapperTest.UTLOEPSTIDSPUNKT;
import static no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapperTest.VAL;
import static no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapperTest.VARSLINGSTYPE;
import static no.nav.varsel.mock.AktoerV2Mock.PERSON_IDENT;
import static no.nav.varsel.repo.TestdataUtil.FUNKSJONELL_FEIL;
import static no.nav.varsel.repo.TestdataUtil.TEKNISK_FEIL;
import static no.nav.varsel.test.TestUtils.aboutNow;
import static no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumer.FØRSTE_GANG_TEKST_TIL_MOTTAKER;
import static no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumer.PREFERERT_KANAL;
import static no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumer.VARSEL_TITTEL;
import static no.nav.varsel.wsconsumer.dokkat.VarselInfoConsumer.VARSEL_URL;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import no.nav.melding.virksomhet.varsel.v1.varsel.AktoerId;
import no.nav.melding.virksomhet.varsel.v1.varsel.ObjectFactory;
import no.nav.melding.virksomhet.varsel.v1.varsel.Varsel;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.code.StatusCode;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.jms.consumer.AbstractConsumerJmsTest;
import no.nav.varsel.jms.consumer.tvarsel001.support.BestillServicemeldingMapperTest;
import no.nav.varsel.jms.to.xml.JmsReply;
import no.nav.varsel.mock.AktoerV2Mock;
import org.junit.Test;

import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.Queue;
import javax.xml.bind.JAXBElement;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Itest for {@link BestillServicemeldingConsumer}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class BestillServicemeldingConsumerTest extends AbstractConsumerJmsTest {

	@Inject
	private Queue bestillServicemeldingQueue;

	@Test
	public void shouldReceieveJms() throws Exception {
		JmsReply response = sendMessage(bestillServicemeldingQueue, createVarsel());

		assertTrue(response != null && response.isOk());
		assertThat(varselbestillingRepo.count(), is(1L));

		Varselbestilling varselbestilling = varselbestillingRepo.findAll().iterator().next();
		assertThat(UUID.fromString(varselbestilling.getVarselbestillingId()).toString(), is(varselbestilling.getVarselbestillingId()));
		assertThat(varselbestilling.getVarslingstype(), is(VARSLINGSTYPE));
		assertThat(varselbestilling.getPreferertKanal(), is(PREFERERT_KANAL));
		assertThat(varselbestilling.getUtlopTidspunkt(), is(equalTo(LocalDateTime.parse(UTLOEPSTIDSPUNKT))));
		assertThat(varselbestilling.getFnr(), is(PERSON_IDENT));
		assertThat(varselbestilling.getAktorId(), is(MOTTAKER));
		assertThat(varselbestilling.getBestillingTidspunkt(), aboutNow());
		assertThat(varselbestilling.getRevarslingIntervall(), nullValue());
		assertThat(varselbestilling.getAntallRevarslinger(), nullValue());
		assertThat(varselbestilling.getNesteVarslingstidspunkt(), nullValue());
		assertThat(varselbestilling.getVarsels(), hasSize(1));

		no.nav.varsel.domain.object.Varsel varsel = varselbestilling.getVarsels().iterator().next();
		assertThat(UUID.fromString(varsel.getVarselId()).toString(), is(varsel.getVarselId()));
		assertThat(varsel.getKanal(), is(KanalCode.DITTNAV));
		assertThat(varsel.getSendtTidspunkt(), aboutNow());
		assertThat(varsel.getDistribusjonTidspunkt(), nullValue());
		assertThat(varsel.getKontaktInfo(), nullValue());
		assertThat(varsel.getStatus(), is(StatusCode.SENDT));
		assertThat(varsel.getFeilbeskrivelse(), nullValue());
		assertThat(varsel.getVarselTittel(), is(VARSEL_TITTEL));
		assertThat(varsel.getVarselTekst(), is(FØRSTE_GANG_TEKST_TIL_MOTTAKER.replace(":mottaker", VAL)));
		assertThat(varsel.getVarselUrl(), is(VARSEL_URL));
		assertThat(varsel.getErRevarsel(), is(false));
	}

	@Test
	public void shouldPutOnBackoutIfFailedWs() throws Exception {
		JAXBElement<Varsel> varsel = createVarsel();
		((AktoerId) varsel.getValue().getMottaker()).setAktoerId(TEKNISK_FEIL);
		Message response = sendMessageListenBoq(bestillServicemeldingQueue, varsel);

		assertThat(response, notNullValue());
	}

	@Test
	public void shouldNotPutOnBackoutIfFailedWsFunksjonell() throws Exception {
		JAXBElement<Varsel> varsel = createVarsel();
		((AktoerId) varsel.getValue().getMottaker()).setAktoerId(FUNKSJONELL_FEIL);
		JmsReply response = sendMessage(bestillServicemeldingQueue, varsel);

		assertTrue(response != null && response.isOk());
	}

	@Test
	public void shouldPutOnBackoutAndRollbackIfFailedAfterDbSave() throws Exception {
		JAXBElement<Varsel> varsel = createVarsel();
		varsel.getValue().getVarslingstype().setValue("feilMqUt");
		Message response = sendMessageListenBoq(bestillServicemeldingQueue, varsel);

		assertThat(response, notNullValue());
		assertThat(varselbestillingRepo.count(), is(0L));
	}

	public static JAXBElement<Varsel> createVarsel() {
		return new ObjectFactory().createVarsel(BestillServicemeldingMapperTest.createVarsel());
	}
}