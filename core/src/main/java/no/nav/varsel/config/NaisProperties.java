package no.nav.varsel.config;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("nais")
public record NaisProperties(
		@NotEmpty
		String tokenEndpoint
) {
}
