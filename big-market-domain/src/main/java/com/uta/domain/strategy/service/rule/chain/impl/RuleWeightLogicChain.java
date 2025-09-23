package com.uta.domain.strategy.service.rule.chain.impl;

import com.uta.domain.strategy.model.entity.RuleActionEntity;
import com.uta.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import com.uta.domain.strategy.repository.IStrategyRepository;
import com.uta.domain.strategy.service.armory.IStrategyDispatch;
import com.uta.domain.strategy.service.rule.chain.AbstractLogicChain;
import com.uta.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import com.uta.domain.strategy.service.rule.filter.factory.DefaultLogicFactory;
import com.uta.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 权重规则链
 */
@Slf4j
@Component("rule_weight")
public class RuleWeightLogicChain extends AbstractLogicChain {

    @Resource
    private IStrategyRepository repository;

    @Resource
    private IStrategyDispatch strategyDispatch;

    private Long userScore = 5500L;

    @Override
    public DefaultChainFactory.StrategyAwardVO logic(String userId, Long strategyId) {
        log.info("【抽奖责任链】-【权重】开始 userId={}, strategyId={}, ruleModel={}", userId, strategyId, ruleModel());

        String ruleValue = repository.getStrategyRuleValue(strategyId, ruleModel());
        String[] splitRuleValue = ruleValue.split(Constants.SPACE);
        Map<Integer, String> map = Arrays.stream(splitRuleValue)
                .map(entry -> entry.split(Constants.COLON))
                .collect(Collectors.toMap(
                        parts -> Integer.parseInt(parts[0]),
                        parts -> parts[0] + Constants.COLON + parts[1],
                        (existing, replacement) -> existing // 重复key时的合并策略（保留现有值）
                ));

        // 4000:102,103,104,105 5000:102,103,104,105,106,107 6000:102,103,104,105,106,107,108,109
        // 对权重key排序
        Integer firstKey = map.keySet().stream()
                .filter(weight -> weight <= userScore)
                .max(Integer::compareTo)
                .orElse(null);

        if (firstKey != null) {
            Integer awardId = strategyDispatch.getRandomAwardId(strategyId, map.get(firstKey));
            log.info("【抽奖责任链】-【权重】接管 userId={}, strategyId={}, ruleModel={}, awardId={}", userId, strategyId, ruleModel(), awardId);
            return DefaultChainFactory.StrategyAwardVO.builder()
                    .awardId(awardId)
                    .logicModel(DefaultChainFactory.LogicModel.RULE_WEIGHT.getCode())
                    .build();
        }

        log.info("【抽奖责任链】-【权重】放行 userId={}, strategyId={}, ruleModel={}", userId, strategyId, ruleModel());
        return next().logic(userId, strategyId);
    }


    @Override
    protected String ruleModel() {
        return "rule_weight";
    }
}
