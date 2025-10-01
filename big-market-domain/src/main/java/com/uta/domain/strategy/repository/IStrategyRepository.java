package com.uta.domain.strategy.repository;

import com.uta.domain.strategy.model.entity.StrategyAwardEntity;
import com.uta.domain.strategy.model.entity.StrategyEntity;
import com.uta.domain.strategy.model.entity.StrategyRuleEntity;
import com.uta.domain.strategy.model.vo.AwardRuleModelVO;
import com.uta.domain.strategy.model.vo.RuleTreeVO;
import com.uta.domain.strategy.model.vo.StrategyAwardStockKeyVO;
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

    void cacheStrategyAwardCount(String key, Integer awardCount);

    Boolean subtractionAwardStock(String key);

    void awardStockConsumeSendMsgQueue(StrategyAwardStockKeyVO build);

    StrategyAwardStockKeyVO takeQueueValue();

    void updateStrategyAwardStock(Long strategyId, Integer awardId);

    StrategyAwardEntity getStrategyAwardEntity(Long strategyId, Integer awardId);

    Long queryStrategyIdByActivityId(Long activityId);

    Integer queryTodayUserRaffleCount(String userId, Long strategyId);
}


