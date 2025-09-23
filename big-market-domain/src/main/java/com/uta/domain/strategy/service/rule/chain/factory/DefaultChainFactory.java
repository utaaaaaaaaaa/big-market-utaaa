package com.uta.domain.strategy.service.rule.chain.factory;

import com.uta.domain.strategy.model.entity.StrategyEntity;
import com.uta.domain.strategy.repository.IStrategyRepository;
import com.uta.domain.strategy.service.rule.chain.ILogicChain;
import lombok.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 责任链默认工厂
 */
@Service
public class DefaultChainFactory {

    private final Map<String, ILogicChain> logicChainMap;

    private final IStrategyRepository repository;

    //spring会将所有实现实现ILogicChain接口的bean收集到一个map中，名字就是component起的那个名字。
    //结合构造器注入，在创建工厂bean的时候会将这个map注入到工厂bean中，所以就可以直接在工厂中使用这个map了。
    //构造器注入的关键点是，参数都要是spring所管理的bean。
    public DefaultChainFactory(Map<String, ILogicChain> logicChainMap, IStrategyRepository repository) {
        this.logicChainMap = logicChainMap;
        this.repository = repository;
    }

    public ILogicChain openLogicChain(Long strategyId) {
        StrategyEntity strategy = repository.getStrategyEntityByStrategyId(strategyId);
        String[] ruleModels = strategy.getRuleModel();

        if (ruleModels == null || ruleModels.length == 0 || !isValid(ruleModels)) {
            return logicChainMap.get(LogicModel.RULE_DEFAULT.getCode());
        }

        ILogicChain chainHead = logicChainMap.get(ruleModels[0]);
        ILogicChain currentChain = chainHead;

        for (int i = 1; i < ruleModels.length; i++) {
            ILogicChain nextChain = logicChainMap.get(ruleModels[i]);
            currentChain = currentChain.appendNext(nextChain);
        }

        currentChain.appendNext(logicChainMap.get(LogicModel.RULE_DEFAULT.getCode()));
        return chainHead;
    }

    private boolean isValid(String[] ruleModels) {
        for (String ruleModel : ruleModels) {
            for (LogicModel logicModel : LogicModel.values()) {
                if (logicModel.getCode().equals(ruleModel)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StrategyAwardVO {
        /** 抽奖奖品ID - 内部流转使用 */
        private Integer awardId;
        /**  */
        private String logicModel;
    }

    @Getter
    @AllArgsConstructor
    public enum LogicModel {

        RULE_DEFAULT("rule_default", "默认抽奖"),
        RULE_BLACKLIST("rule_blacklist", "黑名单抽奖"),
        RULE_WEIGHT("rule_weight", "权重规则"),
        ;

        private final String code;
        private final String info;

    }


}
