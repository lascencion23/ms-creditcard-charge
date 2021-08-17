package com.pring.mschargecredit.service;

import com.pring.mschargecredit.entity.Credit;
import com.pring.mschargecredit.entity.CreditCard;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CreditService {
    Mono<Credit> create(Credit t);

    Flux<Credit> findAll();

    Mono<Credit> findById(String id);

    Mono<Credit> update(Credit t);

    Mono<Boolean> delete(String t);

    Mono<Long> findCountCreditCardId(String t);

    Mono<Double> findTotalConsumptionCreditCardId(String t);

    Mono<CreditCard> findCreditCard(String id);
}
