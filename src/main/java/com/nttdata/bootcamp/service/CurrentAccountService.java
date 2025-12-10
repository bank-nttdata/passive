package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.Passive;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//Interface Service
public interface CurrentAccountService {
    Flux<Passive> findAllCurrentAccount();
    Flux<Passive> findCurrentAccountByCustomer(String dni);
    Mono<Passive> findCurrentAccountByAccountNumber(String accountNumber);
    Mono<Passive> saveCurrentAccount(Passive dataCurrentAccount,Boolean creditCard);
    Mono<Passive> updateCurrentAccount(Passive dataCurrentAccount);
    Mono<Void> deleteCurrentAccount(String accountNumber);

}
