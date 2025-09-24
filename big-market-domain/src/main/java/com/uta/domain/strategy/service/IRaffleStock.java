package com.uta.domain.strategy.service;

import com.uta.domain.strategy.model.vo.StrategyAwardStockKeyVO;

/**
 * 抽奖库存相关接口，获取库存消耗队列
 */
public interface IRaffleStock {

    /**
     * 获取奖品库存消耗队列
     *
     * @return 奖品库存key信息
     * @throws InterruptedException
     */
    StrategyAwardStockKeyVO takeQueueValue() throws InterruptedException;

    /**
     * 更新奖品消耗记录
     *
     * @param strategyId  策略id
     * @param awardId 奖品id
     */
    void updateStrategyAwardStock(Long strategyId, Integer awardId);

}
