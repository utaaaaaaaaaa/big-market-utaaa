package com.uta.domain.strategy.service.armory;

/**
 * @description 策略装配库，负责初始化策略计算
 */
public interface IStrategyArmory {

    /**
     * 根据活动id分配抽奖策略
     * @param activityId
     * @return 分配结果
     */
    boolean assembleLotteryStrategyByActivityId(Long activityId);

    /**
     * 根据策略id分配抽奖策略
     * @param strategyId
     * @return 分配结果
     */
    boolean assembleLotteryStrategy(Long strategyId);

}
