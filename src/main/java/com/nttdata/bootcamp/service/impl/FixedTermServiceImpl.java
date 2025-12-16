package com.nttdata.bootcamp.service.impl;

import com.nttdata.bootcamp.entity.Passive;
import com.nttdata.bootcamp.repository.PassiveRepository;
import com.nttdata.bootcamp.service.FixedTermService;
import com.nttdata.bootcamp.service.PassiveService;
import com.nttdata.bootcamp.util.Constant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FixedTermServiceImpl implements FixedTermService {

    @Autowired
    private PassiveRepository passiveRepository;

    @Autowired
    private PassiveService passiveService;

    // -------------------------------------------------------------------------
    // FIND ALL
    // -------------------------------------------------------------------------
    @Override
    public Flux<Passive> findAllFixedTerm() {
        return passiveRepository.findAll()
                .filter(Passive::getFixedTerm);
    }

    // -------------------------------------------------------------------------
    // FIND BY CUSTOMER
    // -------------------------------------------------------------------------
    @Override
    public Flux<Passive> findFixedTermByCustomer(String dni) {
        return passiveRepository.findAll()
                .filter(x -> x.getFixedTerm() && x.getDni().equals(dni));
    }

    // -------------------------------------------------------------------------
    // FIND BY ACCOUNT NUMBER
    // -------------------------------------------------------------------------
    @Override
    public Mono<Passive> findFixedTermByAccountNumber(String accountNumber) {
        return passiveRepository.findAll()
                .filter(x -> x.getFixedTerm() &&
                        x.getAccountNumber().equals(accountNumber))
                .next();
    }

    // -------------------------------------------------------------------------
    // SAVE
    // -------------------------------------------------------------------------
    @Override
    public Mono<Passive> saveFixedTerm(Passive dataFixedTerm) {
        dataFixedTerm.setFreeCommission(true);
        dataFixedTerm.setCommissionMaintenance(0);
        dataFixedTerm.setMovementsMonthly(true);
        dataFixedTerm.setLimitMovementsMonthly(1);
        dataFixedTerm.setSaving(false);
        dataFixedTerm.setCurrentAccount(false);
        dataFixedTerm.setFixedTerm(true);
        dataFixedTerm.setFlagVip(false);
        dataFixedTerm.setFlagPyme(false);

        // Solo personas → validar si ya tiene cuenta Fixed Term
        Mono<Passive> existing =
                dataFixedTerm.getTypeCustomer().equals(Constant.PERSONAL_CUSTOMER)
                        ? passiveService.searchByFixedTermCustomer(dataFixedTerm)
                        : Mono.empty();

        return existing
                .flatMap(x -> Mono.<Passive>error(
                        new RuntimeException("The customer with DNI " +
                                dataFixedTerm.getDni() +
                                " already has a FixedTerm account")
                ))
                .switchIfEmpty(passiveRepository.save(dataFixedTerm));
    }

    // -------------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------------
    @Override
    public Mono<Passive> updateFixedTerm(Passive dataFixedTerm) {

        return findFixedTermByAccountNumber(dataFixedTerm.getAccountNumber())
                .switchIfEmpty(Mono.error(new RuntimeException(
                        "The account number " + dataFixedTerm.getAccountNumber() + " does not exist")))
                .flatMap(existing -> {
                    existing.setModificationDate(dataFixedTerm.getModificationDate());
                    return passiveRepository.save(existing);
                });
    }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------
    @Override
    public Mono<Void> deleteFixedTerm(String accountNumber) {

        return findFixedTermByAccountNumber(accountNumber)
                .switchIfEmpty(Mono.error(new RuntimeException(
                        "The account number " + accountNumber + " does not exist")))
                .flatMap(passiveRepository::delete);
    }

}
