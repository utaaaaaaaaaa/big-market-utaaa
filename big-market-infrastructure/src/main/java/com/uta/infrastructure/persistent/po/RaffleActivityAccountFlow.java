package com.uta.infrastructure.persistent.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 抽奖活动账户流水表
 * @TableName raffle_activity_account_flow
 */
@TableName(value ="raffle_activity_account_flow")
@Data
public class RaffleActivityAccountFlow implements Serializable {
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
     * 活动ID
     */
    private Long activityId;

    /**
     * 总次数
     */
    private Integer totalCount;

    /**
     * 日次数
     */
    private Integer dayCount;

    /**
     * 月次数
     */
    private Integer monthCount;

    /**
     * 流水ID - 生成的唯一ID
     */
    private String flowId;

    /**
     * 流水渠道（activity-活动领取、sale-购买、redeem-兑换、free-免费赠送）
     */
    private String flowChannel;

    /**
     * 业务ID（外部透传，活动ID、订单ID）
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