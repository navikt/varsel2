package no.nav.varsel.config.alias;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@ConfigurationProperties("listener")
@Validated
public class ListenerProperties {

	@NonNull
	private boolean autoStartup;

}
