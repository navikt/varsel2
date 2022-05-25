package no.nav.varsel.xacml.attributeid;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


/**
 * Unit test for {@link EnvironmentAttributeIds}
 *
 * @author Roar Bjurstrom, Visma Consulting.
 */
public class EnvironmentAttributeIdsTest {

	private static final String ENVIRONMENT_RECIEVER = "urn:nav:ikt:tilgangskontroll:xacml:varsel:environment:environment-receiver";

	@Test
	public void shouldReturnEnvironmentReciever() {
		assertThat(EnvironmentAttributeIds.ATTR_ENVIRONMENT_RECIEVER, is(ENVIRONMENT_RECIEVER));
		assertThat(EnvironmentAttributeIds.ENVIRONMENT_RECIEVER.getURN(), is(ENVIRONMENT_RECIEVER));
	}
}