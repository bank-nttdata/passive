package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.Passive;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//Interface Service
public interface FixedTermService {
    Flux<Passive> findAllFixedTerm();
    Flux<Passive> findFixedTermByCustomer(String dni);
    Mono<Passive> findFixedTermByAccountNumber(String accountNumber);
    Mono<Passive> saveFixedTerm(Passive dataFixedTerm);
    Mono<Passive> updateFixedTerm(Passive dataFixedTerm);
    Mono<Void> deleteFixedTerm(String accountNumber);
}
