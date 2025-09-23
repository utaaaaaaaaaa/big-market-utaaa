package com.uta.domain.strategy.service.rule.chain;

public interface ILogicChainArmory {

    ILogicChain appendNext(ILogicChain next);

    ILogicChain next();

}
