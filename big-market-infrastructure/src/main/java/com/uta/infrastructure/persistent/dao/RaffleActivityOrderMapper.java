package com.uta.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import com.uta.infrastructure.persistent.po.RaffleActivityOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 24333
* @description 针对表【raffle_activity_order(抽奖活动单)】的数据库操作Mapper
* @createDate 2025-09-26 10:50:19
* @Entity persistent.po.RaffleActivityOrder
*/
@Mapper
@DBRouterStrategy(splitTable = true)
public interface RaffleActivityOrderMapper extends BaseMapper<RaffleActivityOrder> {

    @DBRouter
    List<RaffleActivityOrder> queryRaffleActivityOrderByUserId(@Param("userId") String userId);

    @DBRouter
    RaffleActivityOrder queryRaffleActivityOrder(RaffleActivityOrder raffleActivityOrderReq);

    int updateOrderCompleted(RaffleActivityOrder raffleActivityOrderReq);

    @DBRouter
    RaffleActivityOrder queryUnpaidActivityOrder(RaffleActivityOrder raffleActivityOrder);
}




