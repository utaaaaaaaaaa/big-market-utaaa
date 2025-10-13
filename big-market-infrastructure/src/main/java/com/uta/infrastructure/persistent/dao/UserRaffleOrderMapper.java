package com.uta.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import com.uta.infrastructure.persistent.po.UserRaffleOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 24333
* @description 针对表【user_raffle_order(用户抽奖订单表)】的数据库操作Mapper
* @createDate 2025-09-28 06:56:19
* @Entity persistent.pojo.UserRaffleOrder
*/
@Mapper
@DBRouterStrategy(splitTable = true)
public interface UserRaffleOrderMapper extends BaseMapper<UserRaffleOrder> {

    @DBRouter
    UserRaffleOrder queryNoUsedRaffleOrder(UserRaffleOrder userRaffleOrderReq);

    @DBRouter
    int updateUserRaffleOrderStateUsed(UserRaffleOrder userRaffleOrder);
}




