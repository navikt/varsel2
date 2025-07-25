package no.nav.varsel.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "varsel")
public class VarselProperties {

	@Valid
	private Endpoints endpoints = new Endpoints();
	@Valid
	private Serviceuser serviceuser = new Serviceuser();
	@Valid
	private Queues queues = new Queues();
	@Valid
	private Jms jms = new Jms();

	private String url;
	private String appName;

	@Data
	public static class Serviceuser {
		@NotEmpty
		private String username;
		@NotEmpty
		private String password;
	}

	@Data
	public static class Endpoints {
		@NotEmpty
		private String dokmetUrl;
		@Valid
		private AzureEndpoint pdl;
		@Valid
		private AzureEndpoint digdirKrrProxy;
	}

	@Data
	@Valid
	public static class AzureEndpoint {
		@NotEmpty
		private String url;
		@NotEmpty
		private String scope;
	}

	@Data
	public static class Queues {
		@NotNull
		@Valid
		private Queue bestillServicemelding = new Queue();
	}

	@Data
	public static class Queue {
		@NotEmpty
		private String queuename;
		@NotEmpty
		private String funkfeilQueuename;
	}

	@Data
	public static class Jms {
		@NotNull
		private Integer consumerErrorContextSize;
		@NotNull
		private Integer consumerErrorContextTimeSeconds;
		@NotNull
		private Integer consumerErrorRestartDelaySeconds;
	}

}