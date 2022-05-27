package no.nav.varsel.service.to;

import no.nav.varsel.domain.exception.NoJmsBackoutException;
import no.nav.varsel.domain.to.AktoerTo;
import no.nav.varsel.repo.TestdataUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for Tvarsel006 validator
 *
 * @author Roar Bjurstrom, Visma Consulting.
 */
public class BestillVarselToTvarsel006Test {

	private static final String ORG_NR = "orgnr";
	private static final String VARSELTYPE_ID = "no/nav/varsel";
	private static final String EPOST = "test@test.no";

	@Test
	public void shouldValidate() throws Exception {
		createTo().validateTvarsel006Input();
	}

	@Test
	public void shouldValidateMissingMottaker() throws Exception {
		BestillVarselTo to = createTo();
		to.setPersonIdent(null);
		to.setAktoerId(null);
		Executable executable = () -> to.validateTvarsel006Input();
		Exception exception = Assertions.assertThrows(NoJmsBackoutException.class, executable);
		assertEquals(exception.getMessage(),"Validation failed for input, mottaker cannot be empty or missing");
	}

	@Test
	public void shouldValidateMissingVarseltypeId() throws Exception {
		BestillVarselTo to = createTo();
		to.setVarseltypeId(null);
		Executable executable = () -> to.validateTvarsel006Input();
		Exception exception = Assertions.assertThrows(NoJmsBackoutException.class, executable);
		assertEquals(exception.getMessage(),"Validation failed for input, varseltypeId cannot be empty or missing");
	}

	@Test
	public void shouldValidateMissingParamKey() throws Exception {
		BestillVarselTo to = createTo();
		to.getParameters().put(null, "val2");
		Executable executable = () -> to.validateTvarsel006Input();
		Exception exception = Assertions.assertThrows(NoJmsBackoutException.class, executable);
		assertEquals(exception.getMessage(),"Validation failed for input, parameter.key cannot be empty or missing");
	}

	@Test
	public void shouldValidateMissingParamValue() throws Exception {
		BestillVarselTo to = createTo();
		to.getParameters().put("key2", null);
		Executable executable = () -> to.validateTvarsel006Input();
		Exception exception = Assertions.assertThrows(NoJmsBackoutException.class, executable);
		assertEquals(exception.getMessage(),"Validation failed for input, parameter.value cannot be empty or missing");
	}

	@Test
	public void shouldValidateMissingEpostAndMobilnr() throws Exception {
		BestillVarselTo to = createTo();
		to.setMobiltelefonnummer(null);
		to.setEpost(null);
		Executable executable = () -> to.validateTvarsel006Input();
		Exception exception = Assertions.assertThrows(NoJmsBackoutException.class, executable);
		assertEquals(exception.getMessage(),"Validation failed for input, kontaktinformasjon cannot be empty or missing");
	}

	@Test
	public void shouldValidateMissingOrgNr() throws Exception {
		BestillVarselTo to = createTo();
		to.setOrgNr(null);
		Executable executable = () -> to.validateTvarsel006Input();
		Exception exception = Assertions.assertThrows(NoJmsBackoutException.class, executable);
		assertEquals(exception.getMessage(),"Validation failed for input, organisasjonsnummer cannot be empty or missing");
	}

	private static BestillVarselTo createTo() {
		BestillVarselTo to = new BestillVarselTo();
		to.setMottaker(AktoerTo.newAktoerId(TestdataUtil.AKTOR_ID));
		to.setOrgNr(ORG_NR);
		to.setEpost(EPOST);
		to.setVarseltypeId(VARSELTYPE_ID);
		to.getParameters().put("key", "val");
		to.setUtloepstidspunkt(LocalDateTime.now());
		return to;
	}

}