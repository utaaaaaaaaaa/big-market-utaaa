package com.uta.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import com.uta.infrastructure.persistent.po.UserAwardRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 24333
* @description 针对表【user_award_record_000(用户中奖记录表)】的数据库操作Mapper
* @createDate 2025-09-28 06:56:19
* @Entity persistent.pojo.UserAwardRecord000
*/
@Mapper
@DBRouterStrategy(splitTable = true)
public interface UserAwardRecordMapper extends BaseMapper<UserAwardRecord> {

}




