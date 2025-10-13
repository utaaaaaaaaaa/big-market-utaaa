package com.uta.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import com.uta.infrastructure.persistent.po.UserBehaviorRebateOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 24333
* @description 针对表【user_behavior_rebate_order(用户行为返利流水订单表)】的数据库操作Mapper
* @createDate 2025-10-14 05:03:18
* @Entity persistent.pojo.UserBehaviorRebateOrder000
*/
@Mapper
@DBRouterStrategy(splitTable = true)
public interface UserBehaviorRebateOrderMapper extends BaseMapper<UserBehaviorRebateOrder> {

}




