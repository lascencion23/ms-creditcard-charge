package com.pring.mschargecredit.repository;

import com.pring.mschargecredit.entity.Credit;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface CreditRepository extends ReactiveMongoRepository<Credit, String> {

    Flux<Credit> findByCreditCardId(String id);
    Flux<Credit> findByCreditCardCustomerId(String id);
    
}
