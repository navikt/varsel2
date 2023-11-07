package no.nav.varsel.config;

import jakarta.annotation.PreDestroy;
import jakarta.jms.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.messaginghub.pooled.jms.JmsPoolConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * Rydder opp ressurser som Spring ikke gjør selv.
 */
@Slf4j
@ConditionalOnBean(ConnectionFactory.class)
@Component
public class ShutdownHook {

	@Autowired
	private ConnectionFactory connectionFactory;

	@PreDestroy
	public void destroy() {
		log.info("Graceful shutdown - Lukker koblinger til ConnectionFactory pool");
		((JmsPoolConnectionFactory) connectionFactory).clear();
	}
}
