package com.uta.infrastructure.persistent.dao;

import com.uta.infrastructure.persistent.po.UserCreditAccount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 24333
* @description 针对表【user_credit_account(用户积分账户)】的数据库操作Mapper
* @createDate 2025-10-16 00:36:09
* @Entity persistent.po.UserCreditAccount
*/
@Mapper
public interface UserCreditAccountMapper extends BaseMapper<UserCreditAccount> {

    int updateAddAmount(UserCreditAccount userCreditAccountReq);
}




