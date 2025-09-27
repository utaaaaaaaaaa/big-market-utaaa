package com.uta.infrastructure.persistent.dao;

import com.uta.infrastructure.persistent.po.RaffleActivityCount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 24333
* @description 针对表【raffle_activity_count(抽奖活动次数配置表)】的数据库操作Mapper
* @createDate 2025-09-26 10:50:19
* @Entity persistent.po.RaffleActivityCount
*/
@Mapper
public interface RaffleActivityCountMapper extends BaseMapper<RaffleActivityCount> {

    RaffleActivityCount queryRaffleActivityCountByActivityCountId(Long activityCountId);

}




