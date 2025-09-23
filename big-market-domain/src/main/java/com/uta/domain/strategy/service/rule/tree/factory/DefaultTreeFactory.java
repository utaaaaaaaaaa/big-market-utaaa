package com.uta.domain.strategy.service.rule.tree.factory;

import com.uta.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import com.uta.domain.strategy.model.vo.RuleTreeVO;
import com.uta.domain.strategy.service.rule.tree.ILogicTreeNode;
import com.uta.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import com.uta.domain.strategy.service.rule.tree.factory.engine.impl.DecisionTreeEngine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 规则树工厂
 */
@Service
public class DefaultTreeFactory {

    private final Map<String, ILogicTreeNode> logicTreeMap;

    public DefaultTreeFactory(Map<String, ILogicTreeNode> logicTreeMap) {
        this.logicTreeMap = logicTreeMap;
    }

    public IDecisionTreeEngine openDecisionTree(RuleTreeVO ruleTreeVO) {
        return new DecisionTreeEngine(logicTreeMap, ruleTreeVO);
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TreeActionEntity{
        private RuleLogicCheckTypeVO ruleLogicCheckType;
        private StrategyAwardVO strategyAwardVO;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StrategyAwardVO{
        private Integer awardId;
        private String awardRuleValue;
    }

}
