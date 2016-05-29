package no.nav.varsel.repo.config;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

/**
 * Repository Test Config
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@Configuration
@EnableAutoConfiguration
@Import(RepoConfig.class)
public class RepoTestConfig {

	@Bean
	public UserTransaction userTransaction() throws SystemException {
		UserTransactionImp transactionImp = new UserTransactionImp();
		transactionImp.setTransactionTimeout(30);
		return transactionImp;
	}

	@Bean(initMethod = "init", destroyMethod = "close")
	public TransactionManager atomikosTransactionManager() throws SystemException {
		UserTransactionManager transactionManager = new UserTransactionManager();
		transactionManager.setForceShutdown(false);
		return transactionManager;
	}

	@Bean
	public PlatformTransactionManager transactionManager() throws SystemException {
		JtaTransactionManager jtaTransactionManager = new JtaTransactionManager(userTransaction(), atomikosTransactionManager());
		jtaTransactionManager.setAllowCustomIsolationLevels(true);
		return jtaTransactionManager;
	}
}
