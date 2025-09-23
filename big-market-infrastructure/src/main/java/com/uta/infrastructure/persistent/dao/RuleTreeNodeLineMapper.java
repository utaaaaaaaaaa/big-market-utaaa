package com.uta.infrastructure.persistent.dao;

import com.uta.infrastructure.persistent.po.RuleTreeNodeLine;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author 24333
* @description 针对表【rule_tree_node_line】的数据库操作Mapper
* @createDate 2025-09-23 21:41:12
* @Entity com.uta.infrastructure.persistent.po.RuleTreeNodeLine
*/
@Mapper
public interface RuleTreeNodeLineMapper extends BaseMapper<RuleTreeNodeLine> {

    @Select("select * from rule_tree_node_line where tree_id = #{treeId}")
    List<RuleTreeNodeLine> queryRuleTreeNodeLineByTreeId(String treeId);

}




