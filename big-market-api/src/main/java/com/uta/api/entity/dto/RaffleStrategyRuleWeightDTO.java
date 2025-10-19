package com.uta.api.entity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RaffleStrategyRuleWeightDTO implements Serializable {

    // 用户ID
    private String userId;
    // 抽奖活动ID
    private Long activityId;


}
