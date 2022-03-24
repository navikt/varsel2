package no.nav.varsel.jms.producer.varselutsending.support;

import static no.nav.varsel.domain.utility.XmlGregorianConverter.toXmlGregorianCalendar;
import static no.nav.varsel.jms.producer.varselutsending.to.VarselutsendingTo.VarselutsendingToBuilder.aVarselutsendingTo;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import no.nav.melding.virksomhet.varselutsending.v2.varselutsending.AktoerId;
import no.nav.melding.virksomhet.varselutsending.v2.varselutsending.Person;
import no.nav.melding.virksomhet.varselutsending.v2.varselutsending.Varselutsending;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.to.AktoerTo;
import no.nav.varsel.jms.producer.varselutsending.to.VarselutsendingTo.VarselutsendingToBuilder;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

/**
 * Unit test for {@link VarselutsendingMapper}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselutsendingMapperTest {

	private static final LocalDateTime UTLOEPSTIDSPUNKT = LocalDateTime.parse("2016-04-24T15:22:45");
	private static final String VARSELTYPE_ID = "varseltypeId";
	private static final KanalCode KANAL_CODE = KanalCode.DITT_NAV;
	private static final String VARSELID = "varselid";
	private static final String VARSELURL = "varselurl";
	private static final String VARSELTEKST = "varseltekst";
	private static final String VARSELTITTEL = "varseltittel";
	private static final String IDENT = "ident";
	private static final String KONTAKT_INFO = "kontaktInfo";

	private VarselutsendingMapper mapper = new VarselutsendingMapper();

	@Test
	public void shouldMap() throws Exception {
		Varselutsending varselutsending = mapper.map(createVarselutsendingTo().build());

		assertThat(varselutsending.getUtloepstidspunkt(), equalTo(toXmlGregorianCalendar(UTLOEPSTIDSPUNKT)));
		assertThat(varselutsending.getVarseltypeId(), is(VARSELTYPE_ID));
		assertThat(varselutsending.getDistribusjon().getKanal().getValue(), is(KANAL_CODE.getKommunikasjonskanal()));
		assertThat(varselutsending.getDistribusjon().getKontaktinformasjon(), is(KONTAKT_INFO));
		assertThat(varselutsending.getMottaker(), instanceOf(AktoerId.class));
		assertThat(((AktoerId) varselutsending.getMottaker()).getAktoerId(), is(IDENT));
		assertThat(varselutsending.getVarselId(), is(VARSELID));
		assertThat(varselutsending.getVarselURL(), is(VARSELURL));
		assertThat(varselutsending.getVarselTekst(), is(VARSELTEKST));
		assertThat(varselutsending.getVarselTittel(), is(VARSELTITTEL));
	}

	@Test
	public void shouldMapPersonIdent() throws Exception {
		Varselutsending varselutsending = mapper.map(createVarselutsendingTo()
				.mottaker(AktoerTo.newPersonIdent(IDENT)).build());

		assertThat(varselutsending.getMottaker(), instanceOf(Person.class));
		assertThat(((Person) varselutsending.getMottaker()).getIdent(), is(IDENT));
	}

	private VarselutsendingToBuilder createVarselutsendingTo() {
		return aVarselutsendingTo()
				.utloepstidspunkt(UTLOEPSTIDSPUNKT)
				.varseltypeId(VARSELTYPE_ID)
				.kanal(KANAL_CODE)
				.kontaktInformasjon(KONTAKT_INFO)
				.mottaker(AktoerTo.newAktoerId(IDENT))
				.varselId(VARSELID)
				.varselUrl(VARSELURL)
				.varselTekst(VARSELTEKST)
				.varselTittel(VARSELTITTEL);
	}
}