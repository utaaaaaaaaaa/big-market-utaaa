package com.uta.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.scheduling.support.SimpleTriggerContext;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleMatterEntity {

    /**
     * 策略id
     */
    private Long strategyId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 奖品id 【规则类型如果是策略则不需要奖品id】
     */
    private Integer awardId;

    /**
     * 抽奖规则类型 【rule_random 随机值计算， rule_lock 抽奖几次解锁， rule_luck_award 幸运奖（范围内随机积分）】
     */
    private String ruleModel;

}
