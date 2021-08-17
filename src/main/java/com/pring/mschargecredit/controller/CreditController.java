package com.pring.mschargecredit.controller;

import com.pring.mschargecredit.entity.Credit;
import com.pring.mschargecredit.service.CreditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/creditCharge")
public class CreditController {

    @Autowired
    CreditService creditService;

    @GetMapping("list")
    public Flux<Credit> findAll(){
        return creditService.findAll();
    }

    @GetMapping("/find/{id}")
    public Mono<Credit> findById(@PathVariable String id){
        return creditService.findById(id);
    }

    @PostMapping("/create")
    public Mono<ResponseEntity<Credit>> create(@RequestBody Credit credit){

        return creditService.findCreditCard(credit.getCreditCard().getId())
                .flatMap(cc -> creditService.findCountCreditCardId(cc.getId())
                                .filter(count -> {
                                    // VERIFICAR CANTIDAD DE CREDITOS PERMITIDOS
                                    switch (cc.getCustomer().getTypeCustomer().getValue()){
                                        case PERSONAL: return count < 1;
                                        case EMPRESARIAL: return true;
                                        default: return false;
                                    }
                                })                  // VERIFICAR SI LA TARJETA DE CREDITO TIENE SALDO
                                .flatMap(count -> creditService.findTotalConsumptionCreditCardId(cc.getId())
                                                .filter(totalConsumption -> cc.getLimitCredit() >= totalConsumption + credit.getAmount())
                                                .flatMap(totalConsumption -> {
                                                    credit.setCreditCard(cc);
                                                    credit.setDate(LocalDateTime.now());
                                                    return creditService.create(credit);
                                                })
                                )
            )
            .map(c -> new ResponseEntity<>(c , HttpStatus.CREATED))
            .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));


    }

    @PutMapping("/update")
    public Mono<ResponseEntity<Credit>> update(@RequestBody Credit credit) {
        return creditService.findById(credit.getId()) //VERIFICO SI EL CREDITO EXISTE
                .flatMap(ccDB -> creditService.findCreditCard(credit.getCreditCard().getId())
                                .flatMap(cc -> creditService.findTotalConsumptionCreditCardId(cc.getId())
                                                .filter(totalConsumption -> cc.getLimitCredit() >= totalConsumption - ccDB.getAmount() + credit.getAmount())
                                                .flatMap(totalConsumption -> {
                                                    credit.setCreditCard(cc);
                                                    credit.setDate(LocalDateTime.now());
                                                    return creditService.create(credit);
                                                })))
                .map(c -> new ResponseEntity<>(c , HttpStatus.CREATED))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping("/delete/{id}")
    public Mono<ResponseEntity<String>> delete(@PathVariable String id) {
        return creditService.delete(id)
                .filter(deleteCustomer -> deleteCustomer)
                .map(deleteCustomer -> new ResponseEntity<>("Customer Deleted", HttpStatus.ACCEPTED))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}