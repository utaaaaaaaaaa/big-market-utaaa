package com.uta.infrastructure.persistent.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName rule_tree
 */
@TableName(value ="rule_tree")
@Data
public class RuleTree implements Serializable {
    /**
     * 自增ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 规则树ID
     */
    private String treeId;

    /**
     * 规则树名称
     */
    private String treeName;

    /**
     * 规则树描述
     */
    private String treeDesc;

    /**
     * 规则树根入口规则
     */
    private String treeRootRuleKey;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}