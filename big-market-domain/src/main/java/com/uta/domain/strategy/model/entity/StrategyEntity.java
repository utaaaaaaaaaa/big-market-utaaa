package com.uta.domain.strategy.model.entity;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.uta.types.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StrategyEntity {

    /** 抽奖策略ID */
    private Long strategyId;

    /** 抽奖策略描述 */
    private String strategyDesc;

    /** 策略规则模型 */
    private String ruleModels;

    public String[] getRuleModel() {
        if (StringUtils.isBlank(ruleModels)) {
            return null;
        }
        return ruleModels.split(Constants.SPLIT);
    }

    public String getRuleWeight(){
        String[] rules = this.getRuleModel();
        for (String rule : rules) {
            if (rule.contains("rule_weight")) {
                return "rule_weight";
            }
        }
        return null;
    }

}
