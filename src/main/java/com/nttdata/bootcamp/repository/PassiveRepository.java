package com.nttdata.bootcamp.repository;

import com.nttdata.bootcamp.entity.Passive;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PassiveRepository extends ReactiveCrudRepository<Passive, String> {
    Flux<Passive> findByCurrentAccountTrue();

    Flux<Passive> findByDniAndCurrentAccountTrue(String dni);

    Mono<Passive> findByAccountNumberAndCurrentAccountTrue(String accountNumber);
}
