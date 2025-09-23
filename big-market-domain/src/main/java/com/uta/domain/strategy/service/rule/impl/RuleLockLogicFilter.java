package com.uta.domain.strategy.service.rule.impl;

import com.uta.domain.strategy.model.entity.RuleActionEntity;
import com.uta.domain.strategy.model.entity.RuleMatterEntity;
import com.uta.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import com.uta.domain.strategy.repository.IStrategyRepository;
import com.uta.domain.strategy.service.annotation.LogicStrategy;
import com.uta.domain.strategy.service.rule.ILogicFilter;
import com.uta.domain.strategy.service.rule.factory.DefaultLogicFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
@LogicStrategy(logicModel = DefaultLogicFactory.LogicModel.RULE_LOCK)
public class RuleLockLogicFilter implements ILogicFilter<RuleActionEntity.RaffleMidEntity> {

    @Resource
    private IStrategyRepository repository;

    private Long userRaffleCount = 10L;

    @Override
    public RuleActionEntity<RuleActionEntity.RaffleMidEntity> filter(RuleMatterEntity ruleMatter) {
        log.info("【规则过滤】-次数锁 userId:{} strategyId:{} ruleModel:{}", ruleMatter.getUserId(), ruleMatter.getStrategyId(), ruleMatter.getRuleModel());
        String userId = ruleMatter.getUserId();

        String ruleValue = repository.getStrategyRuleValue(ruleMatter.getStrategyId(),ruleMatter.getAwardId(),ruleMatter.getRuleModel());
        long raffleCount = Long.parseLong(ruleValue);

        if (userRaffleCount >= raffleCount) {
            return RuleActionEntity.<RuleActionEntity.RaffleMidEntity>builder()
                    .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                    .build();
        }

        return RuleActionEntity.<RuleActionEntity.RaffleMidEntity>builder()
                .code(RuleLogicCheckTypeVO.TAKE_OVER.getCode())
                .info(RuleLogicCheckTypeVO.TAKE_OVER.getInfo())
                .ruleModel(ruleMatter.getRuleModel())
                .build();
    }
}
