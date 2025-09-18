package com.uta.infrastructure.persistent.dao;

import com.uta.domain.strategy.model.entity.StrategyAwardEntity;
import com.uta.infrastructure.persistent.po.StrategyAward;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author 24333
* @description 针对表【strategy_award】的数据库操作Mapper
* @createDate 2025-09-17 20:40:14
* @Entity persistent.pojo.StrategyAward
*/
@Mapper
public interface StrategyAwardMapper extends BaseMapper<StrategyAward> {

    List<StrategyAwardEntity> queryStrategyAwardListByStrategyId(Long strategyId);

}




