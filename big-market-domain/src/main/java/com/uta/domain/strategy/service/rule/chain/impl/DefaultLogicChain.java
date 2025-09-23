package com.uta.domain.strategy.service.rule.chain.impl;

import com.uta.domain.strategy.service.armory.IStrategyDispatch;
import com.uta.domain.strategy.service.rule.chain.AbstractLogicChain;
import com.uta.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 默认兜底处理责任链节点
 */
@Slf4j
@Component("rule_default")
public class DefaultLogicChain extends AbstractLogicChain {

    @Resource
    private IStrategyDispatch strategyDispatch;

    @Override
    public DefaultChainFactory.StrategyAwardVO logic(String userId, Long strategyId) {
        Integer awardId = strategyDispatch.getRandomAwardId(strategyId);
        log.info("【抽奖责任链】-默认处理 userId={}, strategyId={}, ruleModel={}, awardId={}", userId, strategyId, ruleModel(), awardId);
        return DefaultChainFactory.StrategyAwardVO.builder()
                .awardId(awardId)
                .logicModel(DefaultChainFactory.LogicModel.RULE_DEFAULT.getCode())
                .build();
    }

    @Override
    protected String ruleModel() {
        return "default";
    }
}
