package no.nav.varsel.jms.consumer.tvarsel001.support;

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
import no.nav.varsel.service.tvarsel001.to.BestillServicemeldingTo;
import org.junit.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.time.LocalDateTime;

/**
 * Unit test for {@link BestillServicemeldingMapper}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class BestillServicemeldingMapperTest {

	public static final String UTLOEPSTIDSPUNKT = "2016-04-24T15:22:45";
	public static final LocalDateTime UTLOEPSTIDSPUNKT_LDT = LocalDateTime.parse(UTLOEPSTIDSPUNKT);
	public static final String MOTTAKER = "mottakeren";
	public static final String KEY = "mottaker";
	public static final String VAL = "val";
	private static DatatypeFactory datatypeFactory;

	private BestillServicemeldingMapper mapper = new BestillServicemeldingMapper();

	public static final String VARSLINGSTYPE = "varslingstype";

	static {
		try {
			datatypeFactory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void shouldMap() throws Exception {
		BestillServicemeldingTo to = mapper.map(createVarsel());

		assertThat(to.getAktoerId(), is(MOTTAKER));
		assertThat(to.getPersonIdent(), nullValue());
		assertThat(to.getVarslingstype(), is(VARSLINGSTYPE));
		assertThat(to.getUtloepstidspunkt(), equalTo(UTLOEPSTIDSPUNKT_LDT));
		assertThat(to.getParameters().keySet(), hasSize(1));
		assertThat(to.getParameters().get(KEY), is(VAL));
	}

	@Test
	public void shouldMapPerson() throws Exception {
		Varsel varsel = createVarsel();
		PersonIdent personIdent = new PersonIdent();
		personIdent.setPersonIdent(MOTTAKER);
		varsel.setMottaker(personIdent);
		BestillServicemeldingTo to = mapper.map(varsel);

		assertThat(to.getPersonIdent(), is(MOTTAKER));
		assertThat(to.getAktoerId(), nullValue());
	}

	@Test
	public void shouldMapNullMottaker() throws Exception {
		Varsel varsel = createVarsel();
		varsel.setMottaker(null);
		BestillServicemeldingTo to = mapper.map(varsel);
		assertThat(to.getPersonIdent(), nullValue());
		assertThat(to.getAktoerId(), nullValue());
	}

	@Test
	public void shouldMapNullVarseltype() throws Exception {
		Varsel varsel = createVarsel();
		varsel.setVarslingstype(null);
		BestillServicemeldingTo to = mapper.map(varsel);
		assertThat(to.getVarslingstype(), nullValue());
	}

	@Test
	public void shouldMapNullUtlop() throws Exception {
		Varsel varsel = createVarsel();
		varsel.setUtloepstidspunkt(null);
		BestillServicemeldingTo to = mapper.map(varsel);
		assertThat(to.getUtloepstidspunkt(), nullValue());
	}

	@Test
	public void shouldMapEmptyParameter() throws Exception {
		Varsel varsel = createVarsel();
		varsel.getParameterListe().clear();
		BestillServicemeldingTo to = mapper.map(varsel);
		assertThat(to.getParameters().keySet(), hasSize(0));
	}

	public static Varsel createVarsel() {
		Varsel varsel = new Varsel();
		Varslingstyper varslingstype = new Varslingstyper();
		varslingstype.setValue(VARSLINGSTYPE);
		varsel.setVarslingstype(varslingstype);
		AktoerId aktoerId = new AktoerId();
		aktoerId.setAktoerId(MOTTAKER);
		varsel.setMottaker(aktoerId);
		varsel.setUtloepstidspunkt(datatypeFactory.newXMLGregorianCalendar(UTLOEPSTIDSPUNKT));
		Parameter parameter = new Parameter();
		parameter.setKey(KEY);
		parameter.setValue(VAL);
		varsel.getParameterListe().add(parameter);
		return varsel;
	}

}