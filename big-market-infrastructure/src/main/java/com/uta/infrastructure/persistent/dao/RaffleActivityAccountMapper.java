package com.uta.infrastructure.persistent.dao;

import com.uta.infrastructure.persistent.po.RaffleActivityAccount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 24333
* @description 针对表【raffle_activity_account(抽奖活动账户表)】的数据库操作Mapper
* @createDate 2025-09-26 10:50:19
* @Entity persistent.po.RaffleActivityAccount
*/
@Mapper
public interface RaffleActivityAccountMapper extends BaseMapper<RaffleActivityAccount> {

    int updateAccountQuota(RaffleActivityAccount raffleActivityAccount);
}




