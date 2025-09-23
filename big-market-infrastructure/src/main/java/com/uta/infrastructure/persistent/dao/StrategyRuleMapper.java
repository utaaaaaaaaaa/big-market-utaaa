package com.uta.infrastructure.persistent.dao;

import com.uta.domain.strategy.model.entity.StrategyRuleEntity;
import com.uta.infrastructure.persistent.po.StrategyRule;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
* @author 24333
* @description 针对表【strategy_rule】的数据库操作Mapper
* @createDate 2025-09-17 20:40:14
* @Entity com.uta.infrastructure.persistent.po.StrategyRule
*/
@Mapper
public interface StrategyRuleMapper extends BaseMapper<StrategyRule> {

    @Select("select strategy_id,award_id,\n" +
            "        rule_type,rule_model,rule_value,\n" +
            "        rule_desc from strategy_rule " +
            " where strategy_id = #{strategyId} and rule_model = 'rule_weight'")
    StrategyRuleEntity queryStrategyRuleEntityByStrategyId(Long strategyId);

    String queryStrategyRuleValue(
            @Param("strategyId") Long strategyId,
            @Param("awardId") Integer awardId,
            @Param("ruleModel") String ruleModel
    );
}




