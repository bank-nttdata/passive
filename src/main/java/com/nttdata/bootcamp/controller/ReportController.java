package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.Passive;
import com.nttdata.bootcamp.entity.dto.PassiveDto;
import com.nttdata.bootcamp.service.CurrentAccountService;
import com.nttdata.bootcamp.service.FixedTermService;
import com.nttdata.bootcamp.service.SavingAccountService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/report")
public class ReportController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    private CurrentAccountService currentAccountService;

    @Autowired
    private FixedTermService fixedTermService;

    @Autowired
    private SavingAccountService savingAccountService;

    @GetMapping("/reportAccountsByCustomer/{dni}")
    public Flux<PassiveDto> reportAccountsByCustomer(@PathVariable String dni) {

        Flux<PassiveDto> currentFlux = currentAccountService.findCurrentAccountByCustomer(dni)
                .map(p -> new PassiveDto(
                        p.getDni(),
                        p.getTypeCustomer(),
                        p.getAccountNumber(),
                        "CurrentAccount"
                ));

        Flux<PassiveDto> savingFlux = savingAccountService.findSavingAccountByCustomer(dni)
                .map(p -> new PassiveDto(
                        p.getDni(),
                        p.getTypeCustomer(),
                        p.getAccountNumber(),
                        "SavingAccount"
                ));

        Flux<PassiveDto> fixedFlux = fixedTermService.findFixedTermByCustomer(dni)
                .map(p -> new PassiveDto(
                        p.getDni(),
                        p.getTypeCustomer(),
                        p.getAccountNumber(),
                        "FixedTermAccount"
                ));

        LOGGER.info("Generating report for customer {}", dni);

        return Flux.merge(currentFlux, savingFlux, fixedFlux)
                .doOnNext(dto -> LOGGER.info("Record → {}", dto));
    }
}
