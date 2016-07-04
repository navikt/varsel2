package no.nav.varsel.batch.support;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.inject.Inject;
import javax.sql.DataSource;

/**
 * Tasklet for performing sql commands
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class JdbcTasklet implements Tasklet {

	private DataSource dataSource;
	private String sql;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		new JdbcTemplate(dataSource).execute(sql);
		return RepeatStatus.FINISHED;
	}

	@Inject
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}
}
