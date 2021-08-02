package com.example.configuration;

import com.example.model.GetAccountsRequest;
import com.example.model.GetAccountsResponse;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

	@Value("${kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Value("${kafka.topic.requestreply-topic}")
	private String requestReplyTopic;

	@Value("${kafka.consumergroup}")
	private String consumerGroup;

	@Bean
	public Map<String, Object> producerConfigs() {
		Map<String, Object> props = new HashMap<>();
		// list of host:port pairs used for establishing the initial connections to the Kakfa cluster
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
				bootstrapServers);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
				StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		return props;
	}

	@Bean
	public Map<String, Object> consumerConfigs() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
		return props;
	}

	@Bean
	public ProducerFactory < String, GetAccountsRequest > requestProducerFactory() {
		return new DefaultKafkaProducerFactory <> (producerConfigs());
	}

	@Bean
	public ConsumerFactory < String, GetAccountsResponse > replyConsumerFactory() {
		return new DefaultKafkaConsumerFactory<>(consumerConfigs(),new StringDeserializer(),new JsonDeserializer<>(GetAccountsResponse.class));
	}

	@Bean
	public KafkaMessageListenerContainer < String, GetAccountsResponse > replyListenerContainer() {
		ContainerProperties containerProperties = new ContainerProperties(requestReplyTopic);
		return new KafkaMessageListenerContainer < > (replyConsumerFactory(), containerProperties);
	}

	@Bean
	public ReplyingKafkaTemplate < String, GetAccountsRequest, GetAccountsResponse > replyKafkaTemplate(
			ProducerFactory < String, GetAccountsRequest > pf,
			KafkaMessageListenerContainer < String, GetAccountsResponse > lc) {
		return new ReplyingKafkaTemplate <> (pf, lc);
	}
}