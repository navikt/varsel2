package no.nav.varsel.config;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "varsel")
public class VarselProperties {

	private Endpoints endpoints = new Endpoints();

	@Data
	public static class Endpoints {
		@NotEmpty
		private String dokmetUrl;
	}

}