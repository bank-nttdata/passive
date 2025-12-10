package com.nttdata.bootcamp.service.impl;

import com.nttdata.bootcamp.entity.Passive;
import com.nttdata.bootcamp.entity.dto.CustomerKafkaDto;
import com.nttdata.bootcamp.events.CreatedEventKafka;
import com.nttdata.bootcamp.events.EventKafka;
import com.nttdata.bootcamp.repository.PassiveRepository;
import com.nttdata.bootcamp.service.KafkaService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;

import javax.annotation.PostConstruct;
import java.util.Date;

@Slf4j
@Service
public class KafkaServiceImpl implements KafkaService {

    @Autowired
    private PassiveRepository passiveRepository;

    @Autowired
    private KafkaReceiver<String, EventKafka<?>> kafkaReceiver;

    @PostConstruct
    @Override
    public void startReactiveKafkaConsumer() {

        kafkaReceiver.receive()
                .flatMap(record -> {

                    EventKafka<?> event = record.value();

                    if (event instanceof CreatedEventKafka) {

                        CreatedEventKafka createdEvent = (CreatedEventKafka) event;

                        log.info("Received CREATED event → id={}, data={}",
                                createdEvent.getId(),
                                createdEvent.getData());

                        CustomerKafkaDto dto = createdEvent.getData();

                        Passive p = new Passive();
                        p.setDni(dto.getDni());
                        p.setTypeCustomer(dto.getTypeCustomer());
                        p.setFlagVip(dto.getFlagVip());
                        p.setFlagPyme(dto.getFlagPyme());
                        p.setCreationDate(new Date());

                        return passiveRepository.save(p);
                    }

                    return Mono.empty();
                })
                .doOnError(err -> log.error("Error processing Kafka event", err))
                .subscribe(); // Activa el stream reactivo
    }
}
