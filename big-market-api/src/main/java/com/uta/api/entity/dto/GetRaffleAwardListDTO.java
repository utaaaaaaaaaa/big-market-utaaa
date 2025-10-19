package com.uta.api.entity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class GetRaffleAwardListDTO implements Serializable {

    @Deprecated
    private Long strategyId;

    private Long activityId;

    private String userId;

}
