package com.uta.domain.activity.model.entity;

import lombok.Builder;
import lombok.Data;

/**
 *  参加抽奖活动实体
 */
@Data
@Builder
public class PartakeRaffleActivityEntity {

    private String userId;

    private Long activityId;

}
