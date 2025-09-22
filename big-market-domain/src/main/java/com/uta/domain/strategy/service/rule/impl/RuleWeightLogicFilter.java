package com.uta.domain.strategy.service.rule.impl;

import com.uta.domain.strategy.model.entity.RuleActionEntity;
import com.uta.domain.strategy.model.entity.RuleMatterEntity;
import com.uta.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import com.uta.domain.strategy.repository.IStrategyRepository;
import com.uta.domain.strategy.service.annotation.LogicStrategy;
import com.uta.domain.strategy.service.rule.ILogicFilter;
import com.uta.domain.strategy.service.rule.factory.DefaultLogicFactory;
import com.uta.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@LogicStrategy(logicModel = DefaultLogicFactory.LogicModel.RULE_WIGHT)
public class RuleWeightLogicFilter implements ILogicFilter<RuleActionEntity.RaffleBeforeEntity> {

    @Resource
    private IStrategyRepository repository;

    private Long userScore = 14500L;

    @Override
    public RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> filter(RuleMatterEntity ruleMatter) {
        log.info("【规则过滤】-权重 userId:{} strategyId:{} ruleModel:{}", ruleMatter.getUserId(), ruleMatter.getStrategyId(), ruleMatter.getRuleModel());

        String ruleValue = repository.getStrategyRuleValue(ruleMatter.getStrategyId(), ruleMatter.getAwardId(), ruleMatter.getRuleModel());
        if (!StringUtils.hasText(ruleValue)) {
            return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                    .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                    .build();
        }

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
//                .sorted()
//                .filter(weight -> weight < userScore)
//                .reduce((first, second) -> second) // 取最后一个元素(或者使用这种方法取得满足要求的最大的key);
        if (firstKey != null) {
            return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                    .ruleModel(DefaultLogicFactory.LogicModel.RULE_WIGHT.getCode())
                    .data(RuleActionEntity.RaffleBeforeEntity.builder()
                            .ruleWeightValue(map.get(firstKey))
                            .strategyId(ruleMatter.getStrategyId())
                            .awardId(ruleMatter.getAwardId())
                            .build())
                    .code(RuleLogicCheckTypeVO.TAKE_OVER.getCode())
                    .info(RuleLogicCheckTypeVO.TAKE_OVER.getInfo())
                    .build();
        }

        return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                .build();
    }

}
