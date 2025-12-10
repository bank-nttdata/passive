package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.Passive;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//Interface Service
public interface SavingAccountService {
    Flux<Passive> findAllSavingAccount();
    Flux<Passive> findSavingAccountByCustomer(String dni);
    Mono<Passive> findSavingAccountByAccountNumber(String accountNumber);
    Mono<Passive> saveSavingAccount(Passive dataSavingAccount, Boolean creditCard);
    Mono<Passive> updateSavingAccount(Passive dataSavingAccount);
    Mono<Void> deleteSavingAccount(String accountNumber);
}
