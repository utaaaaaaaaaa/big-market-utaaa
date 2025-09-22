package com.uta.domain.strategy.model.entity;

import com.uta.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleActionEntity<T extends RuleActionEntity.RuffleEntity> {

    private String code = RuleLogicCheckTypeVO.ALLOW.getCode();

    private String info = RuleLogicCheckTypeVO.ALLOW.getInfo();

    private String ruleModel;

    private T data;

    static public class RuffleEntity{

    }

    @EqualsAndHashCode(callSuper = true) //让父类字段也参与equals和hashcode的比较
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    static public class RaffleBeforeEntity extends RuffleEntity{
        /**
         * 策略id
         */
        private Long strategyId;
        /**
         * 权重值key：抽奖前选择不同权重抽奖（不同积分范围对应不同奖品范围）
         */
        private String ruleWeightValue;
        /**
         * 奖品id
         */
        private Integer awardId;
    }

}
