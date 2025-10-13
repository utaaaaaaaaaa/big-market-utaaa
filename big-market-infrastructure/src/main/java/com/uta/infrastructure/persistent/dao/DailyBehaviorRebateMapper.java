package com.uta.infrastructure.persistent.dao;

import com.uta.domain.rebate.model.vo.DailyBehaviorRebateVO;
import com.uta.infrastructure.persistent.po.DailyBehaviorRebate;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author 24333
* @description 针对表【daily_behavior_rebate(日常行为返利活动配置)】的数据库操作Mapper
* @createDate 2025-10-14 05:03:18
* @Entity persistent.pojo.DailyBehaviorRebate
*/
@Mapper
public interface DailyBehaviorRebateMapper extends BaseMapper<DailyBehaviorRebate> {

    List<DailyBehaviorRebateVO> queryDailyBehaviorRebateConfig(String behaviorType);
}




