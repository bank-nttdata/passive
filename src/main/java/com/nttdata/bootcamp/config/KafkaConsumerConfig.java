package com.nttdata.bootcamp.config;

import com.nttdata.bootcamp.events.EventKafka;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {
    @Value("${topic.customer.name:topic_customer}")
    private String topic;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ReceiverOptions<String, EventKafka<?>> kafkaReceiverOptions() {

        JsonDeserializer<EventKafka<?>> jsonDeserializer =
                new JsonDeserializer<>(EventKafka.class);
        jsonDeserializer.addTrustedPackages("*");

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "grupo1");

        ReceiverOptions<String, EventKafka<?>> options =
                ReceiverOptions.<String, EventKafka<?>>create(props)
                        .withKeyDeserializer(new StringDeserializer())
                        .withValueDeserializer(jsonDeserializer)
                        .subscription(Collections.singletonList(topic));

        return options;
    }

    @Bean
    public KafkaReceiver<String, EventKafka<?>> kafkaReceiver(
            ReceiverOptions<String, EventKafka<?>> options
    ) {
        return KafkaReceiver.create(options);
    }
}
