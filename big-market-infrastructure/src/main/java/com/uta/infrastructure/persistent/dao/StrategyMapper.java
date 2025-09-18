package com.uta.infrastructure.persistent.dao;

import com.uta.domain.strategy.model.entity.StrategyEntity;
import com.uta.infrastructure.persistent.po.Strategy;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
* @author 24333
* @description 针对表【strategy】的数据库操作Mapper
* @createDate 2025-09-17 20:40:14
* @Entity persistent.pojo.Strategy
*/
@Mapper
public interface StrategyMapper extends BaseMapper<Strategy> {

    @Select("select strategy_id,strategy_desc,rule_models from strategy where strategy_id = #{strategyId}")
    StrategyEntity queryStrategyEntityByStrategyId(Long strategyId);
}




