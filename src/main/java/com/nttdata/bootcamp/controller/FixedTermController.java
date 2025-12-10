package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.Passive;
import com.nttdata.bootcamp.entity.dto.FixedTermDto;
import com.nttdata.bootcamp.entity.dto.UpdateFixedTermDto;
import com.nttdata.bootcamp.service.FixedTermService;
import com.nttdata.bootcamp.util.Constant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Date;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/fixedTerm")
public class FixedTermController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FixedTermController.class);

    @Autowired
    private FixedTermService fixedTermService;

    // --- FIND ALL ---
    @GetMapping("/findAllFixedTerm")
    public Flux<Passive> findAllFixedTerm() {
        return fixedTermService.findAllFixedTerm()
                .doOnSubscribe(s -> LOGGER.info("Listing all FixedTerm accounts"))
                .doOnNext(p -> LOGGER.info("FixedTerm: {}", p));
    }

    // --- FIND BY DNI ---
    @GetMapping("/findFixedTermByDni/{dni}")
    public Flux<Passive> findFixedTermByDni(@PathVariable String dni) {
        return fixedTermService.findFixedTermByCustomer(dni)
                .doOnSubscribe(s -> LOGGER.info("Listing FixedTerm accounts for DNI {}", dni))
                .doOnNext(p -> LOGGER.info(" → {}", p));
    }

    // --- FIND BY ACCOUNT NUMBER ---
    @GetMapping("/findFixedTermByAccountNumber/{accountNumber}")
    public Mono<Passive> findFixedTermByAccountNumber(@PathVariable String accountNumber) {
        LOGGER.info("Searching FixedTerm account {}", accountNumber);
        return fixedTermService.findFixedTermByAccountNumber(accountNumber);
    }

    // --- SAVE FIXED TERM ---
    @PostMapping("/saveFixedTerm")
    public Mono<Passive> saveFixedTerm(@RequestBody FixedTermDto account) {

        return Mono.fromSupplier(() -> {
                    Passive p = new Passive();
                    p.setDni(account.getDni());
                    p.setTypeCustomer(account.getTypeCustomer());
                    p.setAccountNumber(account.getAccountNumber());
                    p.setCommissionTransaction(0.00);
                    p.setStatus(Constant.PASSIVE_ACTIVE);
                    p.setCreationDate(new Date());
                    p.setModificationDate(new Date());
                    return p;
                })
                .doOnNext(p -> LOGGER.info("Saving FixedTerm: {}", p))
                .flatMap(fixedTermService::saveFixedTerm);
    }

    // --- UPDATE ---
    @PutMapping("/updateFixedTerm/{accountNumber}")
    public Mono<Passive> updateFixedTerm(
            @PathVariable String accountNumber,
            @Valid @RequestBody UpdateFixedTermDto account) {

        return fixedTermService.findFixedTermByAccountNumber(accountNumber)
                .switchIfEmpty(Mono.error(new RuntimeException("Account not found")))
                .flatMap(existing -> {

                    existing.setModificationDate(new Date()); //VERIFICAR
                    //existing.setCommissionMaintenance(account.getCommissionMaintenance());

                    return fixedTermService.updateFixedTerm(existing);
                })
                .doOnNext(p -> LOGGER.info("Updated FixedTerm: {}", p));
    }

    // --- DELETE ---
    @DeleteMapping("/deleteFixedTerm/{accountNumber}")
    public Mono<Void> deleteFixedTerm(@PathVariable String accountNumber) {
        LOGGER.info("Deleting FixedTerm account {}", accountNumber);
        return fixedTermService.deleteFixedTerm(accountNumber)
                .doOnSuccess(v -> LOGGER.info("Deleted {}", accountNumber));
    }
}
