package com.pring.mschargecredit.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.validation.Valid;

@Data
public class SubType {
    @Id
    String id;

    @Valid
    EnumSubType value;

    enum EnumSubType{
        NORMAL, VIP, PYME
    }
}
