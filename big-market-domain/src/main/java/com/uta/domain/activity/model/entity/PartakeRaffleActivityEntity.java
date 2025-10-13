package com.uta.domain.activity.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  参加抽奖活动实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PartakeRaffleActivityEntity {

    private String userId;

    private Long activityId;

}
