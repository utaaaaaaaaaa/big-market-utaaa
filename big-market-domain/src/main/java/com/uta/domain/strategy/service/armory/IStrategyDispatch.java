package com.uta.domain.strategy.service.armory;

/**
 * @description 抽奖策略调度
 */
public interface IStrategyDispatch {

    /**
     * 根据策略id随机抽奖
     * @param strategyId 策略id
     * @return 抽奖结果
     */
    Integer getRandomAwardId(Long strategyId);


    Integer getRandomAwardId(Long strategyId, String ruleWeightValue);

    /**
     * 扣减奖品库存
     * @param strategyId 策略id
     * @param awardId 奖品id
     * @return 结果
     */
    Boolean subtractionAwardStock(Long strategyId, Integer awardId);

}
