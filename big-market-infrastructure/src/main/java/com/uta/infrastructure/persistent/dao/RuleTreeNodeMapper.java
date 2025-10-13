package com.uta.infrastructure.persistent.dao;

import com.uta.infrastructure.persistent.po.RuleTreeNode;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author 24333
* @description 针对表【rule_tree_node】的数据库操作Mapper
* @createDate 2025-09-23 21:41:12
* @Entity com.uta.infrastructure.persistent.po.RuleTreeNode
*/
@Mapper
public interface RuleTreeNodeMapper extends BaseMapper<RuleTreeNode> {

    @Select("select * from rule_tree_node where tree_id = #{treeId}")
    List<RuleTreeNode> queryRuleTreeNodeListByTreeId(String treeId);

    List<RuleTreeNode> queryRuleLocks(String[] treeIds);
}




