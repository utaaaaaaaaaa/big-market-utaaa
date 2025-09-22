package com.uta.domain.strategy.service.rule;

import com.uta.domain.strategy.model.entity.RuleActionEntity;
import com.uta.domain.strategy.model.entity.RuleMatterEntity;

/**
 * 抽奖逻辑过滤接口
 * @param <T> 分别对应抽奖前中后返回的实体类
 */
public interface ILogicFilter<T extends RuleActionEntity.RuffleEntity> {

    RuleActionEntity<T> filter(RuleMatterEntity ruleMatter);

}
