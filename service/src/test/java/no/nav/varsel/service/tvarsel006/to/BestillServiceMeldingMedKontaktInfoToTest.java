package no.nav.varsel.service.tvarsel006.to;

import no.nav.varsel.domain.to.AktoerTo;
import no.nav.varsel.repo.TestdataUtil;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.LocalDateTime;

/**
 * Unit test for Tvarsel006 validator
 *
 * @author Roar Bjurstrom, Visma Consulting.
 */
public class BestillServiceMeldingMedKontaktInfoToTest {

	private static final String ORG_NR = "orgnr";
	private static final String VARSELTYPE_ID = "varsel";
	private static final String EPOST = "test@test.no";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Test
	public void shouldValidate() throws Exception {
		createTo().validateTvarsel006Input();
	}

	@Test
	public void shouldValidateMissingMottaker() throws Exception {
		BestillServiceMeldingMedKontaktInfoTo to = createTo();
		to.setPersonIdent(null);
		to.setAktoerId(null);

		expectedException.expectMessage("mottaker cannot be empty or missing");
		to.validateTvarsel006Input();
	}

	@Test
	public void shouldValidateMissingVarseltypeId() throws Exception {
		BestillServiceMeldingMedKontaktInfoTo to = createTo();
		to.setVarseltypeId(null);

		expectedException.expectMessage("varseltypeId cannot be empty or missing");
		to.validateTvarsel006Input();
	}

	@Test
	public void shouldValidateMissingParamKey() throws Exception {
		BestillServiceMeldingMedKontaktInfoTo to = createTo();
		to.getParameters().put(null, "val2");

		expectedException.expectMessage("parameter.key cannot be empty or missing");
		to.validateTvarsel006Input();
	}

	@Test
	public void shouldValidateMissingParamValue() throws Exception {
		BestillServiceMeldingMedKontaktInfoTo to = createTo();
		to.getParameters().put("key2", null);

		expectedException.expectMessage("parameter.value cannot be empty or missing");
		to.validateTvarsel006Input();
	}

	@Test
	public void shouldValidateMissingEpostAndMobilnr() throws Exception {
		BestillServiceMeldingMedKontaktInfoTo to = createTo();
		to.setMobiltelefonnummer(null);
		to.setEpost(null);

		expectedException.expectMessage("kontaktinformasjon cannot be empty or missing");
		to.validateTvarsel006Input();
	}

	@Test
	public void shouldValidateMissingOrgNr() throws Exception {
		BestillServiceMeldingMedKontaktInfoTo to = createTo();
		to.setOrgNr(null);

		expectedException.expectMessage("organisasjonsnummer cannot be empty or missing");
		to.validateTvarsel006Input();
	}

	private static BestillServiceMeldingMedKontaktInfoTo createTo() {
		BestillServiceMeldingMedKontaktInfoTo to = new BestillServiceMeldingMedKontaktInfoTo();
		to.setMottaker(AktoerTo.newAktoerId(TestdataUtil.AKTOR_ID));
		to.setOrgNr(ORG_NR);
		to.setEpost(EPOST);
		to.setVarseltypeId(VARSELTYPE_ID);
		to.getParameters().put("key", "val");
		to.setUtloepstidspunkt(LocalDateTime.now());
		return to;
	}

}