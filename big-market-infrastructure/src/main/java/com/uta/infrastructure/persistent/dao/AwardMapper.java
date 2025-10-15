package com.uta.infrastructure.persistent.dao;

import com.uta.infrastructure.persistent.po.Award;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author 24333
* @description 针对表【award】的数据库操作Mapper
* @createDate 2025-09-17 20:40:14
* @Entity com.uta.infrastructure.persistent.po.Award
*/
@Mapper
public interface AwardMapper extends BaseMapper<Award> {

    @Select("select * from award limit 3")
    List<Award> getAwardList();

    @Select("select award_config from award where award_id = #{awardId}")
    String queryAwardConfig(Integer awardId);

    @Select("select award_key from award where award_id = #{awardId}")
    String queryAwardKey(Integer awardId);
}




