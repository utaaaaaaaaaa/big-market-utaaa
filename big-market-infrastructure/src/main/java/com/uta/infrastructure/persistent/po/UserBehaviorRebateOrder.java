package com.uta.infrastructure.persistent.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户行为返利流水订单表
 * @TableName user_behavior_rebate_order
 */
@TableName(value ="user_behavior_rebate_order")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserBehaviorRebateOrder implements Serializable {
    /**
     * 自增ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 行为类型（sign 签到、openai_pay 支付）
     */
    private String behaviorType;

    /**
     * 返利描述
     */
    private String rebateDesc;

    /**
     * 返利类型（sku 活动库存充值商品、integral 用户活动积分）
     */
    private String rebateType;

    /**
     * 返利配置【sku值，积分值】
     */
    private String rebateConfig;

    /**
     * 业务ID - 拼接的唯一值
     */
    private String bizId;

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