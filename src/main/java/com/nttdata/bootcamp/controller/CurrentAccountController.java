package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.Passive;
import com.nttdata.bootcamp.entity.dto.CurrentAccountDto;
import com.nttdata.bootcamp.entity.dto.UpdateCurrentAccountDto;
import com.nttdata.bootcamp.service.CurrentAccountService;
import com.nttdata.bootcamp.util.Constant;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Date;

@RestController
@RequestMapping("/currentAccount")
@CrossOrigin("*")
public class CurrentAccountController {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(CurrentAccountController.class);

    @Autowired
    private CurrentAccountService currentAccountService;

    // --- FIND ALL ---
    //All Currents Account Products Registered
    @GetMapping("/findAllCurrentAccounts")
    public Flux<Passive> findAllCurrentAccounts() {
        return currentAccountService.findAllCurrentAccount()
                .doOnSubscribe(s ->
                        LOGGER.info("Listing all current accounts"));
    }

    // --- FIND BY DNI ---
    //Current Account Products Registered by customer dni
    @GetMapping("/findCurrentAccountByDni/{dni}")
    public Flux<Passive> findCurrentAccountByDni(@PathVariable String dni) {
        return currentAccountService.findCurrentAccountByCustomer(dni)
                .doOnSubscribe(s ->
                        LOGGER.info("Listing current accounts for dni {}", dni));
    }

    // --- FIND BY ACCOUNT NUMBER ---
    //Search Current Account by AccountNumber
    @GetMapping("/findCurrentAccountByAccountNumber/{accountNumber}")
    public Mono<Passive> findCurrentAccountByAccountNumber(
            @PathVariable String accountNumber) {
        LOGGER.info("Searching current account {}", accountNumber);
        return currentAccountService.findCurrentAccountByAccountNumber(accountNumber);
    }

    // --- SAVE PERSONAL ---
    //Save Current Account Personal
    @PostMapping("/saveCurrentAccountPersonal")
    public Mono<Passive> saveCurrentAccountPersonal(@RequestBody CurrentAccountDto account) {

        return Mono.fromSupplier(() -> {
                    Passive p = new Passive();
                    p.setDni(account.getDni());
                    p.setTypeCustomer(Constant.PERSONAL_CUSTOMER);
                    p.setAccountNumber(account.getAccountNumber());
                    p.setCommissionMaintenance(account.getCommissionMaintenance());
                    p.setStatus(Constant.PASSIVE_ACTIVE);
                    p.setCreationDate(new Date());
                    p.setModificationDate(new Date());
                    return p;
                }).doOnNext(p ->
                        LOGGER.info("Saving personal current account {}", p)).
                flatMap(p ->
                        currentAccountService.saveCurrentAccount(p, false));
    }

    // --- SAVE BUSINESS ---
    // Save Current Account Business
    @PostMapping("/saveCurrentAccountBusiness/{flagCreditCard}")
    public Mono<Passive> saveCurrentAccountBusiness(
            @RequestBody CurrentAccountDto account,
            @PathVariable Boolean flagCreditCard) {

        Passive p = new Passive();
        p.setRuc(account.getRuc());
        p.setDni(account.getDni());// representante
        p.setTypeCustomer(Constant.BUSINESS_CUSTOMER);
        p.setAccountNumber(account.getAccountNumber());
        p.setCommissionMaintenance(account.getCommissionMaintenance());
        p.setCommissionTransaction(account.getCommissionTransaction());
        p.setStatus(Constant.PASSIVE_ACTIVE);
        p.setFirmante(account.getFirmante());
        p.setCreationDate(new Date());
        p.setModificationDate(new Date());

        LOGGER.info("Saving business current account [accountNumber={}]", p.getAccountNumber());

        return currentAccountService.saveCurrentAccount(p, flagCreditCard);
    }


    // --- UPDATE ---
    //Update Current Account
    @PutMapping("/updateCurrentAccount/{accountNumber}")
    public Mono<Passive> updateCurrentAccount(
            @PathVariable String accountNumber,
            @RequestBody UpdateCurrentAccountDto account) {

        return currentAccountService.findCurrentAccountByAccountNumber(accountNumber)
                .switchIfEmpty(Mono.error(new RuntimeException("Account does not exist")))
                .flatMap(existing -> {
                    existing.setCommissionMaintenance(account.getCommissionMaintenance());
                    existing.setModificationDate(new Date());

                    return currentAccountService.updateCurrentAccount(existing);
                }).doOnNext(p -> LOGGER.info("Updated current account {}", p));
    }

    // --- DELETE ---
    //Delete Current Account
    @DeleteMapping("/deleteCurrentAccount/{accountNumber}")
    public Mono<Void> deleteCurrentAccount(@PathVariable String accountNumber) {
        LOGGER.info("Deleting current account {}", accountNumber);
        return currentAccountService.deleteCurrentAccount(accountNumber)
                .doOnSuccess(v -> LOGGER.info("Deleted {}", accountNumber));
    }
}
