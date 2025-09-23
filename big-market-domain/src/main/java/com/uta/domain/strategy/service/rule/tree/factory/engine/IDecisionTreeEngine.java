package com.uta.domain.strategy.service.rule.tree.factory.engine;

import com.uta.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import com.uta.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;

public interface IDecisionTreeEngine {

    DefaultTreeFactory.StrategyAwardVO process(String userId, Long strategyId, Integer awardId);

}
