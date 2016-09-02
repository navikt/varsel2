package no.nav.varsel.jms.consumer.tvarsel003.support;

import static no.nav.varsel.domain.utility.XmlGregorianConverter.toXmlGregorianCalendar;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.Aktoer;
import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.AktoerId;
import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.Parameter;
import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.Person;
import no.nav.melding.virksomhet.varselmedhandling.v1.varselmedhandling.VarselMedHandling;
import no.nav.varsel.jms.consumer.ObjectMessageWrapper;
import no.nav.varsel.service.to.BestillVarselTo;
import org.apache.activemq.command.ActiveMQMessage;
import org.junit.Before;
import org.junit.Test;

import javax.jms.JMSException;
import javax.jms.Message;
import java.time.LocalDateTime;

/**
 * Unit test for {@link BestillVarselMapper}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class BestillVarselMapperTest {

	public static final String VARSELBESTILLING_ID = "84d31fc5-523d-4ae1-a1d8-449ed2382f61";
	public static final boolean REVARSLING = true;
	public static final String KEY = "mottaker";
	public static final String VAL = "val";
	public static final String AKTOER_ID = "aktoerId";
	public static final String PERSON_IDENT = "personIdent";
	public static final String VARSELTYPE_ID = "varseltypeId";
	public static final LocalDateTime UTLOEPS_TIDSPUNKT = LocalDateTime.parse("2016-06-06T21:21:42");
	public static final String TESTVARSEL_WITH_DIFFERENT_CAPS = "TeStVaRsEl";

	private BestillVarselMapper mapper = new BestillVarselMapper();
	private Message messageWithNoTestvarselProperty;

	@Before
	public void onSetup() throws JMSException {
		messageWithNoTestvarselProperty = new ActiveMQMessage();
	}

	@Test
	public void shouldMap() throws Exception {
		BestillVarselTo to = mapper.map(createVarselBestilling(messageWithNoTestvarselProperty));

		assertCoreProperties(to);
	}

	@Test
	public void mapsTestvarsel() throws JMSException {

		org.apache.activemq.Message messageWithTestvarselTrue = new ActiveMQMessage();
		messageWithTestvarselTrue.setBooleanProperty(BestillVarselTo.TESTVARSEL, true);

		BestillVarselTo to = mapper.map(createVarselBestilling(messageWithTestvarselTrue));

		assertCoreProperties(to);
		assertThat(to.isTestvarsel(), is(true));
	}

	@Test
	public void mapsTestvarselWithDifferentCaps() throws JMSException {
		Message messageWithTestvarselWithDifferentCaps = new ActiveMQMessage();
		messageWithTestvarselWithDifferentCaps.setBooleanProperty(TESTVARSEL_WITH_DIFFERENT_CAPS, true);

		BestillVarselTo to = mapper.map(createVarselBestilling(messageWithTestvarselWithDifferentCaps));

		assertCoreProperties(to);
		assertThat(to.isTestvarsel(), is(true));
	}

	@Test
	public void mapsTestvarselAsTrueWhenStringTypeOfPropertyAndValueIsTrue() throws JMSException {
		Message messageWithTestvarselWithStringType = new ActiveMQMessage();
		messageWithTestvarselWithStringType.setStringProperty(BestillVarselTo.TESTVARSEL, "trUe");

		BestillVarselTo to = mapper.map(createVarselBestilling(messageWithTestvarselWithStringType));

		assertCoreProperties(to);
		assertThat(to.isTestvarsel(), is(true));
	}

	@Test
	public void mapsTestvarselAsFalseWhenSomeWrongTypeOfProperty() throws JMSException {
		Message messageWithTestvarselWithWrongType = new ActiveMQMessage();
		messageWithTestvarselWithWrongType.setLongProperty(BestillVarselTo.TESTVARSEL, 1L);

		BestillVarselTo to = mapper.map(createVarselBestilling(messageWithTestvarselWithWrongType));

		assertCoreProperties(to);
		assertThat(to.isTestvarsel(), is(false));
	}

	@Test
	public void mapsTestvarselAsTrueWhenSomeWrongTypeOfPropertyAndOneOtherWithCorrectTypeHasTrue() throws JMSException {
		Message msgTestvarselWithWrongTypeAndOneWithCorrectTypeHasTrue = new ActiveMQMessage();
		msgTestvarselWithWrongTypeAndOneWithCorrectTypeHasTrue.setLongProperty(BestillVarselTo.TESTVARSEL, 1L);
		msgTestvarselWithWrongTypeAndOneWithCorrectTypeHasTrue.setBooleanProperty(TESTVARSEL_WITH_DIFFERENT_CAPS,true);

		BestillVarselTo to = mapper.map(createVarselBestilling(msgTestvarselWithWrongTypeAndOneWithCorrectTypeHasTrue));

		assertCoreProperties(to);
		assertThat(to.isTestvarsel(), is(true));
	}

	@Test
	public void mapsTestvarselAsTrueWhenOnePropetyHasTrueAndAnotherValueHasFalseRegardlesCaps() throws JMSException {
		Message msgOnePropetyHasTrueAndAnotherValueHasFalseRegardlesCaps = new ActiveMQMessage();
		msgOnePropetyHasTrueAndAnotherValueHasFalseRegardlesCaps.setBooleanProperty(TESTVARSEL_WITH_DIFFERENT_CAPS, true);
		msgOnePropetyHasTrueAndAnotherValueHasFalseRegardlesCaps.setBooleanProperty(BestillVarselTo.TESTVARSEL,false);

		BestillVarselTo to = mapper.map(createVarselBestilling(msgOnePropetyHasTrueAndAnotherValueHasFalseRegardlesCaps));

		assertCoreProperties(to);
		assertThat(to.isTestvarsel(), is(true));
	}

	@Test
	public void mapsTestvarselAsFalseIfNoProperty() {
		BestillVarselTo to = mapper.map(createVarselBestilling(messageWithNoTestvarselProperty));

		assertCoreProperties(to);
		assertThat(to.isTestvarsel(), is(false));
	}

	@Test
	public void mapsTestvarselAsFalseIfPropertyIsFalse() throws JMSException {
		Message messageWithTestVarselFalse = new ActiveMQMessage();
		messageWithTestVarselFalse.setBooleanProperty(BestillVarselTo.TESTVARSEL, false);

		BestillVarselTo to = mapper.map(createVarselBestilling(messageWithTestVarselFalse));

		assertCoreProperties(to);
		assertThat(to.isTestvarsel(), is(false));
	}

	@Test
	public void shouldMapAktoerId() throws Exception {
		ObjectMessageWrapper<VarselMedHandling> varselBestillingWithMessage = createVarselBestilling(messageWithNoTestvarselProperty);
		VarselMedHandling varsel = varselBestillingWithMessage.getObject();
		varsel.setMottaker(createAktoerId());
		BestillVarselTo to = mapper.map(varselBestillingWithMessage);

		assertThat(to.getPersonIdent(), nullValue());
		assertThat(to.getAktoerId(), is(AKTOER_ID));
	}

	@Test
	public void shouldMapNullValues() throws Exception {
		mapper.map(new ObjectMessageWrapper<>(new VarselMedHandling(), messageWithNoTestvarselProperty));
	}

	private void assertCoreProperties(BestillVarselTo to) {
		assertThat(to.getVarselBestillingId(), is(VARSELBESTILLING_ID));
		assertThat(to.isRevarsling(), is(REVARSLING));
		assertThat(to.getPersonIdent(), is(PERSON_IDENT));
		assertThat(to.getAktoerId(), nullValue());
		assertThat(to.getVarseltypeId(), is(VARSELTYPE_ID));
		assertThat(to.getUtloepstidspunkt(), is(UTLOEPS_TIDSPUNKT));
		assertThat(to.getParameters().keySet(), hasSize(1));
		assertThat(to.getParameters().get(KEY), is(VAL));
	}

	public static ObjectMessageWrapper<VarselMedHandling> createVarselBestilling(Message message) {
		VarselMedHandling varsel = new VarselMedHandling();
		varsel.setVarselbestillingId(VARSELBESTILLING_ID);
		varsel.setReVarsel(REVARSLING);
		Parameter parameter = new Parameter();
		parameter.setKey(KEY);
		parameter.setValue(VAL);
		varsel.getParameterListe().add(parameter);
		varsel.setMottaker(createPerson());
		varsel.setVarseltypeId(VARSELTYPE_ID);
		varsel.setUtloepstidspunkt(toXmlGregorianCalendar(UTLOEPS_TIDSPUNKT));
		return new ObjectMessageWrapper<>(varsel, message);
	}

	private static Aktoer createAktoerId() {
		AktoerId aktoerId = new AktoerId();
		aktoerId.setAktoerId(AKTOER_ID);
		return aktoerId;
	}

	private static Aktoer createPerson() {
		Person person = new Person();
		person.setIdent(PERSON_IDENT);
		return person;
	}
}