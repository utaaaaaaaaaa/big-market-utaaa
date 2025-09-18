package com.uta.domain.strategy.repository;

import com.uta.domain.strategy.model.entity.StrategyAwardEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @description 策略仓储接口
 * @author utaa
 */
@Component
public interface IStrategyRepository {

    List<StrategyAwardEntity> getStrategyAwardList(Long strategyId);

    void storeStrategyAwardSearchRateTable(Long strategyId, Integer size, Map<Integer, Integer> shuffleStrategyAwardSearchRateTable);

    int getRateRange(Long strategyId);

    Integer getStrategyAwardAssemble(Long strategyId, Integer i);
}
