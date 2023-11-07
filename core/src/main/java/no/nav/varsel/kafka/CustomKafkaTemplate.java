package no.nav.varsel.kafka;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.core.RoutingKafkaTemplate;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Configuration
public class CustomKafkaTemplate {

	@Bean
	public RoutingKafkaTemplate routingTemplate(ProducerFactory<Object, Object> defaultProducerFactory) {

		// Clone the PF with a different Serializer, register with Spring for shutdown
		Map<String, Object> configs = new HashMap<>(defaultProducerFactory.getConfigurationProperties());
		configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
		DefaultKafkaProducerFactory<Object, Object> dittNavProducerFactory = new DefaultKafkaProducerFactory<>(configs);

		Map<Pattern, ProducerFactory<Object, Object>> map = new LinkedHashMap<>();
		map.put(Pattern.compile("min-side.*"), dittNavProducerFactory);
		map.put(Pattern.compile("teamdokumenthandtering.*"), defaultProducerFactory);
		return new RoutingKafkaTemplate(map);
	}
}
