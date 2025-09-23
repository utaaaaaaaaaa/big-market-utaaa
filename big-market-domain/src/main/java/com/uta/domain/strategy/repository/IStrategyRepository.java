package com.uta.domain.strategy.repository;

import com.uta.domain.strategy.model.entity.StrategyAwardEntity;
import com.uta.domain.strategy.model.entity.StrategyEntity;
import com.uta.domain.strategy.model.entity.StrategyRuleEntity;
import com.uta.domain.strategy.model.vo.AwardRuleModelVO;
import com.uta.domain.strategy.model.vo.RuleTreeVO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @description 策略仓储接口
 * @author utaa
 */
public interface IStrategyRepository {

    List<StrategyAwardEntity> getStrategyAwardList(Long strategyId);

    void storeStrategyAwardSearchRateTable(String key, Integer size, Map<Integer, Integer> shuffleStrategyAwardSearchRateTable);

    int getRateRange(Long strategyId);

    int getRateRange(String key);

    Integer getStrategyAwardAssemble(Long strategyId, Integer i);

    Integer getStrategyAwardAssemble(String key, Integer i);

    StrategyEntity getStrategyEntityByStrategyId(Long strategyId);

    StrategyRuleEntity getStrategyRuleEntityByStrategyId(Long strategyId);

    String getStrategyRuleValue(Long strategyId, Integer awardId, String ruleModel);

    String getStrategyRuleValue(Long strategyId, String ruleModel);

    AwardRuleModelVO getAwardRuleModels(Long strategyId, Integer randomAwardId);

    RuleTreeVO getRuleTreeVOByTreeId(String treeId);

}


