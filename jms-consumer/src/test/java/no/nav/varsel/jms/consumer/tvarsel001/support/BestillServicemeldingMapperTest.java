package no.nav.varsel.jms.consumer.tvarsel001.support;

import static no.nav.varsel.Utils.formatDateTime;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import no.nav.melding.virksomhet.varsel.v1.varsel.AktoerId;
import no.nav.melding.virksomhet.varsel.v1.varsel.Parameter;
import no.nav.melding.virksomhet.varsel.v1.varsel.PersonIdent;
import no.nav.melding.virksomhet.varsel.v1.varsel.Varsel;
import no.nav.melding.virksomhet.varsel.v1.varsel.Varslingstyper;
import no.nav.varsel.jms.consumer.ObjectMessageWrapper;
import no.nav.varsel.service.to.BestillVarselTo;
import org.apache.activemq.command.ActiveMQMessage;
import org.junit.Test;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.time.LocalDateTime;

/**
 * Unit test for {@link BestillServicemeldingMapper}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class BestillServicemeldingMapperTest {

	public static final LocalDateTime UTLOEPSTIDSPUNKT_LDT = LocalDateTime.now().plusHours(1);
	public static final String MOTTAKER = "mottakeren";
	public static final String KEY = "mottaker";
	public static final String VAL = "val";
	private static DatatypeFactory datatypeFactory;

	private BestillServicemeldingMapper mapper = new BestillServicemeldingMapper();
	private Message defaultMessage = new ActiveMQMessage();

	public static final String VARSELTYPE_ID = "varseltypeId";

	static {
		try {
			datatypeFactory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void shouldMap() throws Exception {
		BestillVarselTo to = mapper.map(createVarsel(defaultMessage));

		assertThat(to.getAktoerId(), is(MOTTAKER));
		assertThat(to.getPersonIdent(), nullValue());
		assertThat(to.getVarseltypeId(), is(VARSELTYPE_ID));
		assertThat(formatDateTime(to.getUtloepstidspunkt()), equalTo(formatDateTime(UTLOEPSTIDSPUNKT_LDT)));
		assertThat(to.getParameters().keySet(), hasSize(1));
		assertThat(to.getParameters().get(KEY), is(VAL));
		assertThat(to.isTestvarsel(), is(false));
	}

	@Test
	public void shouldMapPerson() throws Exception {
		ObjectMessageWrapper<Varsel> varsel = createVarsel(defaultMessage);
		PersonIdent personIdent = new PersonIdent();
		personIdent.setPersonIdent(MOTTAKER);
		varsel.getObject().setMottaker(personIdent);
		BestillVarselTo to = mapper.map(varsel);

		assertThat(to.getPersonIdent(), is(MOTTAKER));
		assertThat(to.getAktoerId(), nullValue());
	}

	@Test
	public void shouldMapNullMottaker() throws Exception {
		ObjectMessageWrapper<Varsel> varsel = createVarsel(defaultMessage);
		varsel.getObject().setMottaker(null);
		BestillVarselTo to = mapper.map(varsel);
		assertThat(to.getPersonIdent(), nullValue());
		assertThat(to.getAktoerId(), nullValue());
	}

	@Test
	public void shouldMapNullVarseltype() throws Exception {
		ObjectMessageWrapper<Varsel> varsel = createVarsel(defaultMessage);
		varsel.getObject().setVarslingstype(null);
		BestillVarselTo to = mapper.map(varsel);
		assertThat(to.getVarseltypeId(), nullValue());
	}

	@Test
	public void shouldMapNullUtlop() throws Exception {
		ObjectMessageWrapper<Varsel> varsel = createVarsel(defaultMessage);
		varsel.getObject().setUtloepstidspunkt(null);
		BestillVarselTo to = mapper.map(varsel);
		assertThat(to.getUtloepstidspunkt(), nullValue());
	}

	@Test
	public void shouldMapEmptyParameter() throws Exception {
		ObjectMessageWrapper<Varsel> varsel = createVarsel(defaultMessage);
		varsel.getObject().getParameterListe().clear();
		BestillVarselTo to = mapper.map(varsel);
		assertThat(to.getParameters().keySet(), hasSize(0));
	}

	@Test
	public void mapsTestvarselToTrue() throws JMSException {
		Message messageWithTestvarselTrue = new ActiveMQMessage();
		messageWithTestvarselTrue.setBooleanProperty(BestillVarselTo.TESTVARSEL, true);
		BestillVarselTo to = mapper.map(createVarsel(messageWithTestvarselTrue));
		assertThat(to.isTestvarsel(), is(true));
	}

	@Test
	public void mapsTestvarselToFalse() throws JMSException {
		Message messageWithTestvarselTrue = new ActiveMQMessage();
		messageWithTestvarselTrue.setBooleanProperty(BestillVarselTo.TESTVARSEL, false);
		BestillVarselTo to = mapper.map(createVarsel(messageWithTestvarselTrue));
		assertThat(to.isTestvarsel(), is(false));
	}

	public static ObjectMessageWrapper<Varsel> createVarsel(Message message) {
		return new ObjectMessageWrapper<>(createVarsel(), message);
	}

	public static Varsel createVarsel() {
		Varsel varsel = new Varsel();
		Varslingstyper varslingstype = new Varslingstyper();
		varslingstype.setValue(VARSELTYPE_ID);
		varsel.setVarslingstype(varslingstype);
		AktoerId aktoerId = new AktoerId();
		aktoerId.setAktoerId(MOTTAKER);
		varsel.setMottaker(aktoerId);
		varsel.setUtloepstidspunkt(datatypeFactory.newXMLGregorianCalendar(UTLOEPSTIDSPUNKT_LDT.toString()));
		Parameter parameter = new Parameter();
		parameter.setKey(KEY);
		parameter.setValue(VAL);
		varsel.getParameterListe().add(parameter);
		return varsel;
	}
}