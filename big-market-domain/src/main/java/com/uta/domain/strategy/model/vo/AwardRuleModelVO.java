package com.uta.domain.strategy.model.vo;

import com.uta.domain.strategy.service.rule.filter.factory.DefaultLogicFactory;
import com.uta.types.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 策略奖品对应规则值对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AwardRuleModelVO {

    private String ruleModels;

    public String[] getRaffleMidRuleList(){
        String[] split = ruleModels.split(Constants.SPLIT);
        List<String> ruleModelList = new ArrayList<>();
        for (String s : split) {
            if (DefaultLogicFactory.LogicModel.isMid(s)){
                ruleModelList.add(s);
            }
        }
        return ruleModelList.toArray(new String[0]);
    }

}
