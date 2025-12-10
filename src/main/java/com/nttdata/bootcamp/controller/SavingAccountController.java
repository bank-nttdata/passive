package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.Passive;
import com.nttdata.bootcamp.entity.dto.SavingAccountDto;
import com.nttdata.bootcamp.entity.dto.UpdateSavingAccountDto;
import com.nttdata.bootcamp.util.Constant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nttdata.bootcamp.service.SavingAccountService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

import javax.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/saving")
public class SavingAccountController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SavingAccountController.class);

    @Autowired
    private SavingAccountService savingAccountService;

    // --- FIND ALL ---
    @GetMapping("/findAllSavingsAccounts")
    public Flux<Passive> findAllSavingsAccounts() {
        return savingAccountService.findAllSavingAccount()
                .doOnSubscribe(s -> LOGGER.info("Listing all savings accounts"))
                .doOnNext(p -> LOGGER.info(" → {}", p));
    }

    // --- FIND BY DNI ---
    @GetMapping("/findSavingAccountByDni/{dni}")
    public Flux<Passive> findSavingAccountByDni(@PathVariable String dni) {
        return savingAccountService.findSavingAccountByCustomer(dni)
                .doOnSubscribe(s -> LOGGER.info("Listing savings accounts for DNI {}", dni))
                .doOnNext(p -> LOGGER.info(" → {}", p));
    }

    // --- FIND BY ACCOUNT NUMBER ---
    @GetMapping("/findSavingAccountByAccountNumber/{accountNumber}")
    public Mono<Passive> findSavingAccountByAccountNumber(@PathVariable String accountNumber) {
        LOGGER.info("Searching saving account {}", accountNumber);
        return savingAccountService.findSavingAccountByAccountNumber(accountNumber);
    }

    // --- SAVE ---
    @PostMapping("/saveSavingAccount/{flagCreditCard}")
    public Mono<Passive> saveSavingAccount(
            @RequestBody SavingAccountDto account,
            @PathVariable Boolean flagCreditCard) {

        return Mono.fromSupplier(() -> {
                    Passive p = new Passive();
                    p.setDni(account.getDni());
                    p.setTypeCustomer(account.getTypeCustomer());
                    p.setAccountNumber(account.getAccountNumber());
                    p.setLimitMovementsMonthly(account.getLimitMovementsMonthly());
                    p.setCommissionTransaction(account.getCommissionTransaction());
                    p.setStatus(Constant.PASSIVE_ACTIVE);
                    p.setCreationDate(new Date());
                    p.setModificationDate(new Date());
                    return p;
                })
                .doOnNext(p -> LOGGER.info("Saving saving account {}", p))
                .flatMap(p -> savingAccountService.saveSavingAccount(p, flagCreditCard));
    }

    // --- UPDATE ---
    @PutMapping("/updateSavingAccount/{accountNumber}")
    public Mono<Passive> updateSavingAccount(
            @PathVariable String accountNumber,
            @Valid @RequestBody UpdateSavingAccountDto account) {

        return savingAccountService.findSavingAccountByAccountNumber(accountNumber)
                .switchIfEmpty(Mono.error(new RuntimeException("Saving account not found")))
                .flatMap(existing -> {

                    existing.setLimitMovementsMonthly(account.getLimitMovementsMonthly());
                    existing.setModificationDate(new Date());

                    return savingAccountService.updateSavingAccount(existing);
                })
                .doOnNext(p -> LOGGER.info("Updated saving account {}", p));
    }

    // --- DELETE ---
    @DeleteMapping("/deleteSavingAccount/{accountNumber}")
    public Mono<Void> deleteSavingAccount(@PathVariable String accountNumber) {
        LOGGER.info("Deleting saving account {}", accountNumber);
        return savingAccountService.deleteSavingAccount(accountNumber)
                .doOnSuccess(v -> LOGGER.info("Deleted {}", accountNumber));
    }
}
