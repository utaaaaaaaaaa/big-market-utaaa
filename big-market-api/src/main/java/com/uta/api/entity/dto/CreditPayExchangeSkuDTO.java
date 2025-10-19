package com.uta.api.entity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CreditPayExchangeSkuDTO implements Serializable {

    private String userId;
    private Long sku;

}

