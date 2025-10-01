package com.uta.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import com.uta.infrastructure.persistent.po.RaffleActivityAccountDay;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 24333
* @description 针对表【raffle_activity_account_day(抽奖活动账户表-日次数)】的数据库操作Mapper
* @createDate 2025-09-28 06:56:19
* @Entity persistent.pojo.RaffleActivityAccountDay
*/
@Mapper
public interface RaffleActivityAccountDayMapper extends BaseMapper<RaffleActivityAccountDay> {

    int updateActivityAccountSubtractionQuota(RaffleActivityAccountDay build);

    @DBRouter
    RaffleActivityAccountDay queryActivityAccountDayByUserId(RaffleActivityAccountDay raffleActivityAccountDayReq);

}




