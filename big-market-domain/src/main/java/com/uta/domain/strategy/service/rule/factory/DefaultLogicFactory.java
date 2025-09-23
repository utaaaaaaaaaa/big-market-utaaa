package com.uta.domain.strategy.service.rule.factory;

import com.uta.domain.strategy.model.entity.RuleActionEntity;
import com.uta.domain.strategy.service.annotation.LogicStrategy;
import com.uta.domain.strategy.service.rule.ILogicFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DefaultLogicFactory {

    public Map<String, ILogicFilter<?>> logicFilterMap = new ConcurrentHashMap<>();

    public DefaultLogicFactory(List<ILogicFilter<?>> logicFilters) {
        logicFilters.forEach(logic -> {
            LogicStrategy strategy = AnnotationUtils.findAnnotation(logic.getClass(), LogicStrategy.class);
            if (null != strategy) {
                logicFilterMap.put(strategy.logicModel().getCode(), logic);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public <T extends RuleActionEntity.RuffleEntity> Map<String, ILogicFilter<T>> openLogicFilter() {
        return (Map<String, ILogicFilter<T>>) (Map<?, ?>) logicFilterMap;
    }

    @Getter
    @AllArgsConstructor
    public enum LogicModel {

        RULE_WEIGHT("rule_weight","【抽奖前规则】根据抽奖权重返回可抽奖范围KEY","before"),
        RULE_BLACKLIST("rule_blacklist","【抽奖前规则】黑名单规则过滤，命中黑名单则直接返回","before"),
        RULE_LOCK("rule_lock","【抽奖中规则】抽奖n次后，对应奖品解锁","mid"),
        RULE_LUCK_AWARD("rule_luck_award","【抽奖后规则】兜底奖品","after")
        ;

        private final String code;
        private final String info;
        private final String type;

        public static boolean isMid(String code){
            return "mid".equals(fromCode(code).getType());
        }

        public static boolean isAfter(String code){
            return "after".equals(fromCode(code).getType());
        }

        public static LogicModel fromCode(String code) {
            for (LogicModel model : LogicModel.values()) {
                if (model.getCode().equals(code)) {
                    return model;
                }
            }
            throw new IllegalArgumentException("No enum constant with code: " + code);
        }


    }

}
