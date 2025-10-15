package com.uta.domain.award.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 奖品分发实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DistributeAwardEntity {

    private String userId;
    private String orderId;
    private Integer awardId;
    private String awardConfig;

}
