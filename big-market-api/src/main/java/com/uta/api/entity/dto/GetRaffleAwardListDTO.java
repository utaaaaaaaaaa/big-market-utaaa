package com.uta.api.entity.dto;

import lombok.Data;

@Data
public class GetRaffleAwardListDTO {

    @Deprecated
    private Long strategyId;

    private Long activityId;

    private String userId;

}
