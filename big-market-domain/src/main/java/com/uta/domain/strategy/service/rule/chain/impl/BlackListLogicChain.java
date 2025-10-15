package com.uta.domain.strategy.service.rule.chain.impl;

import com.uta.domain.strategy.repository.IStrategyRepository;
import com.uta.domain.strategy.service.rule.chain.AbstractLogicChain;
import com.uta.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import com.uta.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 黑名单责任链
 */
@Slf4j
@Component("rule_blacklist")
public class BlackListLogicChain extends AbstractLogicChain {

    @Resource
    private IStrategyRepository repository;

    @Override
    public DefaultChainFactory.StrategyAwardVO logic(String userId, Long strategyId) {
        log.info("【抽奖责任链】-【黑名单】开始 userId={}, strategyId={}, ruleModel={}", userId, strategyId, ruleModel());
        String ruleValue = repository.getStrategyRuleValue(strategyId, ruleModel());
        String[] splitRuleValue = ruleValue.split(Constants.COLON);
        Integer awardId = Integer.parseInt(splitRuleValue[0]);

        // 100:user001,user002,user003
        // 对用户进行黑名单过滤
        String[] userBlackIds = splitRuleValue[1].split(Constants.SPLIT);
        for (String userBlackId : userBlackIds) {
            if (userId.equals(userBlackId)) {
                log.info("【抽奖责任链】-【黑名单】接管 userId={}, strategyId={}, ruleModel={}, awardId={}", userId, strategyId, ruleModel(), awardId);
                return DefaultChainFactory.StrategyAwardVO.builder()
                        .awardId(awardId)
                        .logicModel(DefaultChainFactory.LogicModel.RULE_BLACKLIST.getCode())
                        // 黑名单奖品默认配置0.01 - 1积分，也可以写入库中
                        .awardRuleValue("0.01,1")
                        .build();
            }
        }

        log.info("【抽奖责任链】-【黑名单】放行 userId={}, strategyId={}, ruleModel={}", userId, strategyId, ruleModel());
        return next().logic(userId, strategyId);
    }

    @Override
    protected String ruleModel() {
        return "rule_blacklist";
    }
}
