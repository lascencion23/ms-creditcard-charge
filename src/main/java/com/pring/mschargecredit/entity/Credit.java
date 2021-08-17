package com.pring.mschargecredit.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@Document("Credit")
@AllArgsConstructor
@NoArgsConstructor
public class Credit {
    @Id
    private String id;

    private CreditCard creditCard;

    private Double amount;

    private LocalDateTime date;

}
