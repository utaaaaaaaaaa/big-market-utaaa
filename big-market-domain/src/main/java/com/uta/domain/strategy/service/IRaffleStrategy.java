package com.uta.domain.strategy.service;

import com.uta.domain.strategy.model.entity.RaffleAwardEntity;
import com.uta.domain.strategy.model.entity.RaffleFactorEntity;

/**
 * @description 抽奖策略接口
 */
public interface IRaffleStrategy {

    /**
     * 执行抽奖
     * @param raffleFactor 抽奖因子（有关抽奖执行的参数）
     * @return 返回抽奖奖品
     */
    RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactor);

}
