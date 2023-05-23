package no.nav.varsel.jms.consumer.tvarsel001.support;

import no.nav.melding.virksomhet.varsel.v1.varsel.XMLAktoerId;
import no.nav.melding.virksomhet.varsel.v1.varsel.XMLParameter;
import no.nav.melding.virksomhet.varsel.v1.varsel.XMLPersonIdent;
import no.nav.melding.virksomhet.varsel.v1.varsel.XMLVarsel;
import no.nav.melding.virksomhet.varsel.v1.varsel.XMLVarslingstyper;
import no.nav.varsel.jms.consumer.ObjectMessageWrapper;
import no.nav.varsel.service.to.BestillVarselTo;
import org.apache.activemq.command.ActiveMQMessage;
import org.junit.jupiter.api.Test;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.time.LocalDateTime;

import static no.nav.varsel.Utils.formatDateTime;
import static no.nav.varsel.domain.utility.XmlGregorianConverter.toJodaDateTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class BestillServicemeldingMapperTest {

	public static final LocalDateTime UTLOEPSTIDSPUNKT_LDT = LocalDateTime.now().plusHours(1);
	public static final String MOTTAKER = "mottakeren";
	public static final String KEY = "mottaker";
	public static final String VAL = "val";
	private static final DatatypeFactory datatypeFactory;

	private final BestillServicemeldingMapper mapper = new BestillServicemeldingMapper();
	private final Message defaultMessage = new ActiveMQMessage();

	public static final String VARSELTYPE_ID = "varseltypeId";

	static {
		try {
			datatypeFactory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void shouldMap() {
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
	public void shouldMapPerson() {
		ObjectMessageWrapper<XMLVarsel> varsel = createVarsel(defaultMessage);
		XMLPersonIdent personIdent = new XMLPersonIdent();
		personIdent.setPersonIdent(MOTTAKER);
		varsel.getObject().setMottaker(personIdent);
		BestillVarselTo to = mapper.map(varsel);

		assertThat(to.getPersonIdent(), is(MOTTAKER));
		assertThat(to.getAktoerId(), nullValue());
	}

	@Test
	public void shouldMapNullMottaker() {
		ObjectMessageWrapper<XMLVarsel> varsel = createVarsel(defaultMessage);
		varsel.getObject().setMottaker(null);
		BestillVarselTo to = mapper.map(varsel);
		assertThat(to.getPersonIdent(), nullValue());
		assertThat(to.getAktoerId(), nullValue());
	}

	@Test
	public void shouldMapNullVarseltype() {
		ObjectMessageWrapper<XMLVarsel> varsel = createVarsel(defaultMessage);
		varsel.getObject().setVarslingstype(null);
		BestillVarselTo to = mapper.map(varsel);
		assertThat(to.getVarseltypeId(), nullValue());
	}

	@Test
	public void shouldMapNullUtlop() {
		ObjectMessageWrapper<XMLVarsel> varsel = createVarsel(defaultMessage);
		varsel.getObject().setUtloepstidspunkt(null);
		BestillVarselTo to = mapper.map(varsel);
		assertThat(to.getUtloepstidspunkt(), nullValue());
	}

	@Test
	public void shouldMapEmptyParameter() {
		ObjectMessageWrapper<XMLVarsel> varsel = createVarsel(defaultMessage);
		varsel.getObject().getParameterListes().clear();
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

	public static ObjectMessageWrapper<XMLVarsel> createVarsel(Message message) {
		return new ObjectMessageWrapper<>(createVarsel(), message);
	}

	public static XMLVarsel createVarsel() {
		XMLVarsel varsel = new XMLVarsel();
		XMLVarslingstyper varslingstype = new XMLVarslingstyper();
		varslingstype.setValue(VARSELTYPE_ID);
		varsel.setVarslingstype(varslingstype);
		XMLAktoerId aktoerId = new XMLAktoerId();
		aktoerId.setAktoerId(MOTTAKER);
		varsel.setMottaker(aktoerId);
		varsel.setUtloepstidspunkt(toJodaDateTime(UTLOEPSTIDSPUNKT_LDT));
		XMLParameter parameter = new XMLParameter();
		parameter.setKey(KEY);
		parameter.setValue(VAL);
		varsel.getParameterListes().add(parameter);
		return varsel;
	}
}