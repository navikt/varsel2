package no.nav.varsel.batch.support;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Unit test for {@link JdbcTasklet}
 *
 * @author Roar Bjurstrom, Visma Consulting.
 */
@RunWith(MockitoJUnitRunner.class)
public class JdbcTaskletTest {

	private static final String SQL = "SELECT * FROM dual";

	@Mock
	private JdbcTemplate jdbcTemplate;
	@InjectMocks
	private JdbcTasklet jdbcTasklet;

	@Test
	public void shouldRunSQL() throws Exception {
		jdbcTasklet.setSql(SQL);

		assertThat(jdbcTasklet.execute(null, null), is(RepeatStatus.FINISHED));
		verify(jdbcTemplate).execute(SQL);
	}
}