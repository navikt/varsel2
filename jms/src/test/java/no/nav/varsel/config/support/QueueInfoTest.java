package no.nav.varsel.config.support;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Unit test for {@link QueueInfo}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class QueueInfoTest {
	@Test
	public void shouldGetDescription() throws Exception {
		String string = QueueInfo.BESTILL_SERVICEMELDING.getDescription();
		assertThat(string, is("direction=IN fasitAlias=varsel_bestill_servicemelding"));
	}

}