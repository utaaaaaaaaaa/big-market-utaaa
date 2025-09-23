package com.uta.infrastructure.persistent.dao;

import com.uta.infrastructure.persistent.po.RuleTree;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
* @author 24333
* @description 针对表【rule_tree】的数据库操作Mapper
* @createDate 2025-09-23 21:41:12
* @Entity com.uta.infrastructure.persistent.po.RuleTree
*/
@Mapper
public interface RuleTreeMapper extends BaseMapper<RuleTree> {

    @Select("select * from rule_tree where tree_id = #{treeId} limit 1")
    RuleTree queryRuleTreeByTreeId(String treeId);
}




