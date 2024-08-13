package no.nav.varsel.jms.consumer.tvarsel001.support;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import no.nav.melding.virksomhet.varsel.v1.varsel.XMLAktoerId;
import no.nav.melding.virksomhet.varsel.v1.varsel.XMLParameter;
import no.nav.melding.virksomhet.varsel.v1.varsel.XMLPersonIdent;
import no.nav.melding.virksomhet.varsel.v1.varsel.XMLVarsel;
import no.nav.melding.virksomhet.varsel.v1.varsel.XMLVarslingstyper;
import no.nav.varsel.jms.consumer.ObjectMessageWrapper;
import no.nav.varsel.service.to.BestillVarselTo;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static no.nav.varsel.Utils.formatDateTime;
import static no.nav.varsel.domain.utility.DateTimeConverter.toJodaDateTime;
import static no.nav.varsel.service.to.BestillVarselTo.TESTVARSEL;
import static no.nav.varsel.service.to.BestillVarselTo.VARSELBESTILLING_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class BestillServicemeldingMapperTest {

	@Mock
	private Message mockActiveMqMessageTestvarselFalse;
	@Mock
	private Message mockActiveMqMessageTestvarselTrue;
	@Mock
	private Message mockActiveMqMessageVarselbestillingIdPresent;
	@Mock
	private Message mockActiveMqMessageVarselbestillingIdNotPresent;

	public static final LocalDateTime UTLOEPSTIDSPUNKT_LDT = LocalDateTime.now().plusHours(1);
	public static final String MOTTAKER = "mottakeren";
	public static final String KEY = "mottaker";
	public static final String VAL = "val";

	private final BestillServicemeldingMapper mapper = new BestillServicemeldingMapper();

	public static final String VARSELTYPE_ID = "varseltypeId";
	private static final String VARSELBESTILLINGID_HEADER_VALUE = "varselbestillingId";

	@BeforeEach
	public void setTestvarsel() throws JMSException {
		MockitoAnnotations.openMocks(this);
		Mockito.doReturn(false).when(mockActiveMqMessageTestvarselFalse).getBooleanProperty(TESTVARSEL);
		Mockito.doReturn(true).when(mockActiveMqMessageTestvarselTrue).getBooleanProperty(TESTVARSEL);
		Mockito.doReturn(VARSELBESTILLINGID_HEADER_VALUE).when(mockActiveMqMessageVarselbestillingIdPresent).getStringProperty(VARSELBESTILLING_ID);
		Mockito.doReturn(null).when(mockActiveMqMessageVarselbestillingIdNotPresent).getStringProperty(VARSELBESTILLING_ID);
	}

	@Test
	public void shouldMap() {
		BestillVarselTo to = mapper.map(createVarsel(mockActiveMqMessageTestvarselFalse));

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
		ObjectMessageWrapper<XMLVarsel> varsel = createVarsel(mockActiveMqMessageTestvarselFalse);
		XMLPersonIdent personIdent = new XMLPersonIdent();
		personIdent.setPersonIdent(MOTTAKER);
		varsel.getObject().setMottaker(personIdent);
		BestillVarselTo to = mapper.map(varsel);

		assertThat(to.getPersonIdent(), is(MOTTAKER));
		assertThat(to.getAktoerId(), nullValue());
	}

	@Test
	public void shouldMapNullMottaker() {
		ObjectMessageWrapper<XMLVarsel> varsel = createVarsel(mockActiveMqMessageTestvarselFalse);
		varsel.getObject().setMottaker(null);
		BestillVarselTo to = mapper.map(varsel);
		assertThat(to.getPersonIdent(), nullValue());
		assertThat(to.getAktoerId(), nullValue());
	}

	@Test
	public void shouldMapNullVarseltype() {
		ObjectMessageWrapper<XMLVarsel> varsel = createVarsel(mockActiveMqMessageTestvarselFalse);
		varsel.getObject().setVarslingstype(null);
		BestillVarselTo to = mapper.map(varsel);
		assertThat(to.getVarseltypeId(), nullValue());
	}

	@Test
	public void shouldMapNullUtlop() {
		ObjectMessageWrapper<XMLVarsel> varsel = createVarsel(mockActiveMqMessageTestvarselFalse);
		varsel.getObject().setUtloepstidspunkt(null);
		BestillVarselTo to = mapper.map(varsel);
		assertThat(to.getUtloepstidspunkt(), nullValue());
	}

	@Test
	public void shouldMapEmptyParameter() {
		ObjectMessageWrapper<XMLVarsel> varsel = createVarsel(mockActiveMqMessageTestvarselFalse);
		varsel.getObject().getParameterListes().clear();
		BestillVarselTo to = mapper.map(varsel);
		assertThat(to.getParameters().keySet(), hasSize(0));
	}

	@Test
	public void messageWithTestvarselTrue() {
		Assertions.assertThat(mapper.map(createVarsel(mockActiveMqMessageTestvarselTrue)).isTestvarsel()).isTrue();
	}

	@Test
	public void messageWithTestvarselFalse() {
		Assertions.assertThat(mapper.map(createVarsel(mockActiveMqMessageTestvarselFalse)).isTestvarsel()).isFalse();
	}

	@Test
	public void messageWithVarselbestillingIdPresent() {
		Assertions.assertThat(mapper.map(createVarsel(mockActiveMqMessageVarselbestillingIdPresent)).getVarselBestillingId())
				.isEqualTo(VARSELBESTILLINGID_HEADER_VALUE);
	}

	@Test
	public void messageWithVarselbestillingIdNotPresent() {
		Assertions.assertThat(mapper.map(createVarsel(mockActiveMqMessageVarselbestillingIdNotPresent)).getVarselBestillingId())
				.isNull();
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