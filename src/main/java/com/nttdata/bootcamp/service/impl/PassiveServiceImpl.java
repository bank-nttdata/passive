package com.nttdata.bootcamp.service.impl;

import com.nttdata.bootcamp.entity.Passive;
import com.nttdata.bootcamp.repository.PassiveRepository;
import com.nttdata.bootcamp.service.PassiveService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public class PassiveServiceImpl implements PassiveService {

    @Autowired
    private PassiveRepository passiveRepository;

    // -------------------------------------------------------------------------
    // SAVING
    // -------------------------------------------------------------------------
    @Override
    public Mono<Passive> searchBySavingCustomer(Passive dataPersonalCustomer) {
        return passiveRepository.findAll()
                .filter(x -> x.getDni().equals(dataPersonalCustomer.getDni())
                        && x.getTypeCustomer().equals(dataPersonalCustomer.getTypeCustomer())
                        && Boolean.TRUE.equals(x.getSaving()))
                .next();
    }

    // -------------------------------------------------------------------------
    // CURRENT ACCOUNT
    // -------------------------------------------------------------------------
    @Override
    public Mono<Passive> searchByCurrentCustomer(Passive dataPersonalCustomer) {
        return passiveRepository.findAll()
                .filter(x -> x.getDni().equals(dataPersonalCustomer.getDni())
                        && x.getTypeCustomer().equals(dataPersonalCustomer.getTypeCustomer())
                        && Boolean.TRUE.equals(x.getCurrentAccount()))
                .next();
    }

    // -------------------------------------------------------------------------
    // FIXED TERM
    // -------------------------------------------------------------------------
    @Override
    public Mono<Passive> searchByFixedTermCustomer(Passive dataPersonalCustomer) {
        return passiveRepository.findAll()
                .filter(x -> x.getDni().equals(dataPersonalCustomer.getDni())
                        && x.getTypeCustomer().equals(dataPersonalCustomer.getTypeCustomer())
                        && Boolean.TRUE.equals(x.getFixedTerm()))
                .next();
    }

}
