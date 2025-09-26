package com.uta.infrastructure.persistent.dao;

import com.uta.domain.strategy.model.entity.StrategyAwardEntity;
import com.uta.domain.strategy.model.vo.AwardRuleModelVO;
import com.uta.infrastructure.persistent.po.StrategyAward;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
* @author 24333
* @description 针对表【strategy_award】的数据库操作Mapper
* @createDate 2025-09-17 20:40:14
* @Entity com.uta.infrastructure.persistent.po.StrategyAward
*/
@Mapper
public interface StrategyAwardMapper extends BaseMapper<StrategyAward> {

    List<StrategyAwardEntity> queryStrategyAwardListByStrategyId(Long strategyId);

    @Select("select rule_models from strategy_award where strategy_id = #{strategyId} and award_id = #{randomAwardId}")
    AwardRuleModelVO queryAwardRuleModel(
            @Param("strategyId") Long strategyId,
            @Param("randomAwardId") Integer awardId
    );

    @Update("update strategy_award set award_count_surplus = award_count_surplus - 1" +
            " where strategy_id = #{strategyId} and award_id = #{awardId} and award_count_surplus > 0")
    void updateStrategyAwardStock(
            @Param("strategyId") Long strategyId,
            @Param("awardId") Integer awardId
    );

    StrategyAwardEntity queryStrategyAwardEntity(
            @Param("strategyId") Long strategyId,
            @Param("awardId") Integer awardId
    );
}




