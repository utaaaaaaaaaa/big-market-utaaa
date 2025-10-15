package com.uta.domain.award.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 用户积分奖品实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreditAwardEntity {

    private String userId;
    private BigDecimal creditAmount;

}
