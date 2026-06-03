package com.aicodereviewer.worker.config;

import com.aicodereviewer.common.KafkaTopics;
import com.aicodereviewer.common.events.ReviewCompletedEvent;
import com.aicodereviewer.common.events.ReviewRequestedEvent;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.ExponentialBackOff;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WorkerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public NewTopic reviewRequestedTopic() {
        return new NewTopic(KafkaTopics.REVIEW_REQUESTED, 3, (short) 1);
    }

    @Bean
    public NewTopic reviewCompletedTopic() {
        return new NewTopic(KafkaTopics.REVIEW_COMPLETED, 3, (short) 1);
    }

    @Bean
    public NewTopic reviewDltTopic() {
        return new NewTopic(KafkaTopics.REVIEW_DLT, 3, (short) 1);
    }

    @Bean
    public ConsumerFactory<String, ReviewRequestedEvent> reviewRequestedConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "worker-service");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        JsonDeserializer<ReviewRequestedEvent> deserializer = new JsonDeserializer<>(ReviewRequestedEvent.class);
        deserializer.addTrustedPackages("com.aicodereviewer.common.events", "com.aicodereviewer.common.dto", "com.aicodereviewer.common.enums");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ProducerFactory<String, ReviewCompletedEvent> reviewCompletedProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, ReviewCompletedEvent> reviewCompletedKafkaTemplate() {
        return new KafkaTemplate<>(reviewCompletedProducerFactory());
    }

    @Bean
    public ProducerFactory<String, Object> deadLetterProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, Object> deadLetterKafkaTemplate() {
        return new KafkaTemplate<>(deadLetterProducerFactory());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ReviewRequestedEvent> reviewRequestedKafkaListenerContainerFactory(
        KafkaTemplate<String, Object> deadLetterKafkaTemplate
    ) {
        ConcurrentKafkaListenerContainerFactory<String, ReviewRequestedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(reviewRequestedConsumerFactory());

        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
            deadLetterKafkaTemplate,
            (record, ex) -> new TopicPartition(KafkaTopics.REVIEW_DLT, record.partition())
        );

        ExponentialBackOff backOff = new ExponentialBackOff(1000L, 2.0);
        backOff.setMaxElapsedTime(Duration.ofSeconds(30).toMillis());

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
}
