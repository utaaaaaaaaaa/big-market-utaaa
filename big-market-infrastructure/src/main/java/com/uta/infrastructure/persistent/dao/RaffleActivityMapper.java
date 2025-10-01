package com.uta.infrastructure.persistent.dao;

import com.uta.infrastructure.persistent.po.RaffleActivity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

/**
* @author 24333
* @description 针对表【raffle_activity(抽奖活动表)】的数据库操作Mapper
* @createDate 2025-09-26 10:50:19
* @Entity persistent.po.RaffleActivity
*/
@Mapper
public interface RaffleActivityMapper extends BaseMapper<RaffleActivity> {

    @Select("select * from raffle_activity where activity_id = #{id}")
    RaffleActivity queryRaffleActivityByActivityId(Long id);

    @Select("select strategy_id from raffle_activity where activity_id = #{activityId}")
    Long queryStrategyIdByActivityId(Long activityId);

    @Select("select activity_id from raffle_activity where strategy_id = #{strategyId}")
    Long queryActivityIdByStrategyId(Long strategyId);
}




