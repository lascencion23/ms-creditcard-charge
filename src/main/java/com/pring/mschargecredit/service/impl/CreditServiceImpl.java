package com.pring.mschargecredit.service.impl;

import com.pring.mschargecredit.entity.Credit;
import com.pring.mschargecredit.entity.CreditCard;
import com.pring.mschargecredit.repository.CreditRepository;
import com.pring.mschargecredit.service.CreditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CreditServiceImpl implements CreditService {

	private final WebClient webClient;
	private final ReactiveCircuitBreaker reactiveCircuitBreaker;
	
	String uri = "http://gateway:8090/api/ms-creditcard/creditcard/find/{id}";
	
	public CreditServiceImpl(ReactiveResilience4JCircuitBreakerFactory circuitBreakerFactory) {
		this.webClient = WebClient.builder().baseUrl(this.uri).build();
		this.reactiveCircuitBreaker = circuitBreakerFactory.create("creditcard");
	}

    @Autowired
    CreditRepository creditRepository;

    // Plan A
    @Override
    public Mono<CreditCard> findCreditCard(String id) {
		return reactiveCircuitBreaker.run(webClient.get().uri(this.uri,id).accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(CreditCard.class),
				throwable -> {
					return this.getDefaultCreditCard();
				});
    }
    
    // Plan B
  	public Mono<CreditCard> getDefaultCreditCard() {
  		Mono<CreditCard> creditCard = Mono.just(new CreditCard("0", null, null,null,null,null));
  		return creditCard;
  	}
    
    @Override
    public Mono<Credit> create(Credit t) {
        return creditRepository.save(t);
    }

    @Override
    public Flux<Credit> findAll() {
        return creditRepository.findAll();
    }

    @Override
    public Mono<Credit> findById(String id) {
        return creditRepository.findById(id);
    }

    @Override
    public Mono<Credit> update(Credit t) {
        return creditRepository.save(t);
    }

    @Override
    public Mono<Boolean> delete(String t) {
        return creditRepository.findById(t)
                .flatMap(credit -> creditRepository.delete(credit).then(Mono.just(Boolean.TRUE)))
                .defaultIfEmpty(Boolean.FALSE);
    }

    @Override
    public Mono<Long> findCountCreditCardId(String t) {
        return  creditRepository.findByCreditCardId(t).count();
    }

    @Override
    public Mono<Double> findTotalConsumptionCreditCardId(String t) {
        return  creditRepository.findByCreditCardId(t)
                .collectList()
                .map(credit -> credit.stream().mapToDouble(cdt -> cdt.getAmount()).sum());
    }

}
