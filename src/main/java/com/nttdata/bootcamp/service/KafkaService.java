package com.nttdata.bootcamp.service;

public interface KafkaService {

    /**
     * Inicia el consumer reactivo de Kafka usando Reactor Kafka.
     * Este método se ejecuta automáticamente desde la implementación
     * (usualmente anotado con @PostConstruct).
     */
    void startReactiveKafkaConsumer();
}
