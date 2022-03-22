package no.nav.varsel.config.support;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

/**
 * Unit test for {@link QueueInfo}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class QueueInfoTest {

	@Test
	public void shouldGetDescription() throws Exception {
		String string = QueueInfo.BESTILL_SERVICEMELDING.getDescription();
		assertThat(string, is("direction=IN fasitAlias=VARSELPRODUKSJON.VARSLINGER"));
	}

}