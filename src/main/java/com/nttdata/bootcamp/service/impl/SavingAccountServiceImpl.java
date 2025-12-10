package com.nttdata.bootcamp.service.impl;

import com.nttdata.bootcamp.entity.Passive;
import com.nttdata.bootcamp.repository.PassiveRepository;
import com.nttdata.bootcamp.service.PassiveService;
import com.nttdata.bootcamp.service.SavingAccountService;
import com.nttdata.bootcamp.util.Constant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SavingAccountServiceImpl implements SavingAccountService {

    @Autowired
    private PassiveRepository passiveRepository;

    @Autowired
    private PassiveService passiveService;

    // -------------------------------------------------------------------------
    // FIND ALL
    // -------------------------------------------------------------------------
    @Override
    public Flux<Passive> findAllSavingAccount() {
        return passiveRepository.findAll()
                .filter(Passive::getSaving);
    }

    // -------------------------------------------------------------------------
    // FIND BY CUSTOMER
    // -------------------------------------------------------------------------
    @Override
    public Flux<Passive> findSavingAccountByCustomer(String dni) {
        return passiveRepository.findAll()
                .filter(x -> x.getSaving() && x.getDni().equals(dni));
    }

    // -------------------------------------------------------------------------
    // FIND BY ACCOUNT NUMBER
    // -------------------------------------------------------------------------
    @Override
    public Mono<Passive> findSavingAccountByAccountNumber(String accountNumber) {
        return passiveRepository.findAll()
                .filter(x -> x.getSaving() &&
                        x.getAccountNumber().equals(accountNumber))
                .next();
    }

    // -------------------------------------------------------------------------
    // SAVE (REACTIVO)
    // -------------------------------------------------------------------------
    @Override
    public Mono<Passive> saveSavingAccount(Passive dataSavingAccount, Boolean creditCard) {

        // Valores por defecto Saving
        dataSavingAccount.setFreeCommission(true);
        dataSavingAccount.setCommissionMaintenance(0);
        dataSavingAccount.setMovementsMonthly(true);
        dataSavingAccount.setSaving(true);
        dataSavingAccount.setCurrentAccount(false);
        dataSavingAccount.setFixedTerm(false);
        dataSavingAccount.setFlagPyme(false);

        // Tratamiento si llegó con tarjeta
        if (creditCard) {
            dataSavingAccount.setDailyAverage(true);
            dataSavingAccount.setFlagVip(true);
        } else {
            dataSavingAccount.setDailyAverage(false);
            dataSavingAccount.setFlagVip(false);
        }

        // Validación → Solo clientes PERSONALES deben tener 1 sola cuenta saving
        Mono<Passive> existing =
                dataSavingAccount.getTypeCustomer().equals(Constant.PERSONAL_CUSTOMER)
                        ? passiveService.searchBySavingCustomer(dataSavingAccount)
                        : Mono.empty();

        return existing
                .flatMap(__ -> Mono.<Passive>error(
                        new RuntimeException("The customer with DNI " +
                                dataSavingAccount.getDni() + " already has a Saving Account")))
                .switchIfEmpty(passiveRepository.save(dataSavingAccount));
    }

    // -------------------------------------------------------------------------
    // UPDATE (REACTIVO)
    // -------------------------------------------------------------------------
    @Override
    public Mono<Passive> updateSavingAccount(Passive dataSavingAccount) {

        return findSavingAccountByAccountNumber(dataSavingAccount.getAccountNumber())
                .switchIfEmpty(Mono.error(new RuntimeException(
                        "The account number " + dataSavingAccount.getAccountNumber() + " does not exist")))
                .flatMap(existing -> {

                    existing.setLimitMovementsMonthly(dataSavingAccount.getLimitMovementsMonthly());
                    existing.setModificationDate(dataSavingAccount.getModificationDate());

                    return passiveRepository.save(existing);
                });
    }

    // -------------------------------------------------------------------------
    // DELETE (REACTIVO)
    // -------------------------------------------------------------------------
    @Override
    public Mono<Void> deleteSavingAccount(String accountNumber) {

        return findSavingAccountByAccountNumber(accountNumber)
                .switchIfEmpty(Mono.error(new RuntimeException(
                        "The account number " + accountNumber + " does not exist")))
                .flatMap(passiveRepository::delete);
    }
}
