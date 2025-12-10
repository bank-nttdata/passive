package com.nttdata.bootcamp.service.impl;

import com.nttdata.bootcamp.entity.Passive;
import com.nttdata.bootcamp.repository.PassiveRepository;
import com.nttdata.bootcamp.service.CurrentAccountService;
import com.nttdata.bootcamp.service.PassiveService;
import com.nttdata.bootcamp.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//Service implementation
@Service
public class CurrentAccountServiceImpl implements CurrentAccountService {

    @Autowired
    private PassiveRepository passiveRepository;

    @Autowired
    private PassiveService passiveService;

    // --- FIND ALL ---
    @Override
    public Flux<Passive> findAllCurrentAccount() {
        return passiveRepository.findByCurrentAccountTrue();
    }

    // --- FIND BY DNI ---
    @Override
    public Flux<Passive> findCurrentAccountByCustomer(String dni) {
        return passiveRepository.findByDniAndCurrentAccountTrue(dni);
    }

    // --- FIND BY ACCOUNT NUMBER ---
    @Override
    public Mono<Passive> findCurrentAccountByAccountNumber(String accountNumber) {
        return passiveRepository.findByAccountNumberAndCurrentAccountTrue(accountNumber);
    }

    // --- SAVE ---
    @Override
    public Mono<Passive> saveCurrentAccount(Passive dataCurrentAccount, Boolean creditCard) {

        // configurar campos base
        dataCurrentAccount.setFlagVip(false);
        dataCurrentAccount.setMovementsMonthly(false);
        dataCurrentAccount.setLimitMovementsMonthly(0);
        dataCurrentAccount.setSaving(false);
        dataCurrentAccount.setCurrentAccount(true);
        dataCurrentAccount.setFixedTerm(false);

        if (creditCard) {
            dataCurrentAccount.setFreeCommission(true);
            dataCurrentAccount.setCommissionMaintenance(0);
            dataCurrentAccount.setFlagPyme(true);
        } else {
            dataCurrentAccount.setFreeCommission(false);
            dataCurrentAccount.setCommissionMaintenance(1);
            dataCurrentAccount.setFlagPyme(false);
        }

        Mono<Passive> validationMono =
                dataCurrentAccount.getTypeCustomer().equals(Constant.PERSONAL_CUSTOMER)
                        ? passiveService.searchByCurrentCustomer(dataCurrentAccount)
                        : Mono.empty();

        return validationMono
                .flatMap(__ -> Mono.<Passive>error(
                        new RuntimeException("The customer with DNI "
                                + dataCurrentAccount.getDni()
                                + " already has an account")))
                .switchIfEmpty(passiveRepository.save(dataCurrentAccount));
    }

    // --- UPDATE ---
    @Override
    public Mono<Passive> updateCurrentAccount(Passive dataCurrentAccount) {

        return findCurrentAccountByAccountNumber(dataCurrentAccount.getAccountNumber())
                .switchIfEmpty(Mono.error(new RuntimeException(
                        "Account " + dataCurrentAccount.getAccountNumber() + " does not exist")))
                .flatMap(existing -> {

                    existing.setCommissionMaintenance(dataCurrentAccount.getCommissionMaintenance());
                    existing.setModificationDate(dataCurrentAccount.getModificationDate());

                    return passiveRepository.save(existing);
                });
    }

    // --- DELETE ---
    @Override
    public Mono<Void> deleteCurrentAccount(String accountNumber) {

        return findCurrentAccountByAccountNumber(accountNumber)
                .switchIfEmpty(Mono.error(new RuntimeException(
                        "Account " + accountNumber + " does not exist")))
                .flatMap(passiveRepository::delete);
    }
}
