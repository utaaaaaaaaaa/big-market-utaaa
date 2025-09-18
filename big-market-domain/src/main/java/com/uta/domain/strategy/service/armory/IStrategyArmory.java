package com.uta.domain.strategy.service.armory;

/**
 * @description 策略装配库，负责初始化策略计算
 */
public interface IStrategyArmory {

    /**
     * 根据策略id分配抽奖策略
     * @param strategyId
     */
    boolean assembleLotteryStrategy(Long strategyId);

    /**
     * 根据策略id随机抽奖
     * @param strategyId
     * @return
     */
    Integer getRandomAwardId(Long strategyId);

}
