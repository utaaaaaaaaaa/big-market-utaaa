package com.uta.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import com.uta.infrastructure.persistent.po.UserCreditOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 24333
* @description 针对表【user_credit_order_000(用户积分订单记录)】的数据库操作Mapper
* @createDate 2025-10-16 12:01:02
* @Entity persistent.po.UserCreditOrder000
*/
@Mapper
@DBRouterStrategy(splitTable = true)
public interface UserCreditOrderMapper extends BaseMapper<UserCreditOrder> {

}




