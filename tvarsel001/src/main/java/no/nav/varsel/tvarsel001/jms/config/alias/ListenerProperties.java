package no.nav.varsel.tvarsel001.jms.config.alias;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@ToString
@ConfigurationProperties("listener")
@Validated
public class ListenerProperties {

	private boolean autoStartup;

}
