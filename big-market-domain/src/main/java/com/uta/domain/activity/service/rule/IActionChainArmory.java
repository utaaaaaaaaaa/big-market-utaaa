package com.uta.domain.activity.service.rule;

public interface IActionChainArmory {

    IActionChain appendNext(IActionChain next);

    IActionChain next();

}
