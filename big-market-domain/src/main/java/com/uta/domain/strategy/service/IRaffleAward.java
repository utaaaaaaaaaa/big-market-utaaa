package com.uta.domain.strategy.service;

import com.uta.domain.strategy.model.entity.StrategyAwardEntity;

import java.util.List;

/**
 * 策略奖品接口
 */
public interface IRaffleAward {

    /**
     * 根据策略id查询奖品列表
     *
     * @param strategyId 策略id
     * @return 奖品列表
     */
    List<StrategyAwardEntity> getRaffleAwardList(Long strategyId);

    /**
     * 根据活动id查询奖品列表
     *
     * @param activityId 活动id
     * @return 奖品列表
     */
    List<StrategyAwardEntity> getRaffleAwardListByActivityId(Long activityId);

}
