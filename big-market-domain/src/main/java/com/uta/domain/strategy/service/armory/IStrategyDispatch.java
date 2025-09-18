package com.uta.domain.strategy.service.armory;

/**
 * @description 抽奖策略调度
 */
public interface IStrategyDispatch {

    /**
     * 根据策略id随机抽奖
     * @param strategyId
     * @return 抽奖结果
     */
    Integer getRandomAwardId(Long strategyId);


    Integer getRandomAwardId(Long strategyId, String ruleWeightValue);

}
