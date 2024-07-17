package no.nav.varsel.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Ekstra konfigurasjon for databasen
 */
@Data
@ConfigurationProperties("database")
public class DataSourceAdditionalProperties {
	private String onshosts;
	private int poolsize = 20;
}
