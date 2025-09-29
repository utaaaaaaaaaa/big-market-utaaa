package com.uta.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import com.uta.infrastructure.persistent.po.RaffleActivityAccountMonth;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 24333
* @description 针对表【raffle_activity_account_month(抽奖活动账户表-月次数)】的数据库操作Mapper
* @createDate 2025-09-28 06:56:19
* @Entity persistent.pojo.RaffleActivityAccountMonth
*/
@Mapper
public interface RaffleActivityAccountMonthMapper extends BaseMapper<RaffleActivityAccountMonth> {

    int updateActivityAccountSubtractionQuota(RaffleActivityAccountMonth build);

    @DBRouter
    RaffleActivityAccountMonth queryActivityAccountMonthByUserId(RaffleActivityAccountMonth raffleActivityAccountMonthReq);
}




