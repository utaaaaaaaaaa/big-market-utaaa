package com.uta.infrastructure.persistent.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName strategy_award
 */
@TableName(value ="strategy_award")
@Data
public class StrategyAward implements Serializable {
    /**
     * 自增ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 抽奖策略ID
     */
    private Long strategyId;

    /**
     * 抽奖奖品ID - 内部流转使用
     */
    private Integer awardId;

    /**
     * 抽奖奖品标题
     */
    private String awardTitle;

    /**
     * 抽奖奖品副标题
     */
    private String awardSubtitle;

    /**
     * 奖品库存总量
     */
    private Integer awardCount;

    /**
     * 奖品库存剩余
     */
    private Integer awardCountSurplus;

    /**
     * 奖品中奖概率
     */
    private BigDecimal awardRate;

    /**
     * 规则模型，rule配置的模型同步到此表，便于使用
     */
    private String ruleModels;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        StrategyAward other = (StrategyAward) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getStrategyId() == null ? other.getStrategyId() == null : this.getStrategyId().equals(other.getStrategyId()))
            && (this.getAwardId() == null ? other.getAwardId() == null : this.getAwardId().equals(other.getAwardId()))
            && (this.getAwardTitle() == null ? other.getAwardTitle() == null : this.getAwardTitle().equals(other.getAwardTitle()))
            && (this.getAwardSubtitle() == null ? other.getAwardSubtitle() == null : this.getAwardSubtitle().equals(other.getAwardSubtitle()))
            && (this.getAwardCount() == null ? other.getAwardCount() == null : this.getAwardCount().equals(other.getAwardCount()))
            && (this.getAwardCountSurplus() == null ? other.getAwardCountSurplus() == null : this.getAwardCountSurplus().equals(other.getAwardCountSurplus()))
            && (this.getAwardRate() == null ? other.getAwardRate() == null : this.getAwardRate().equals(other.getAwardRate()))
            && (this.getRuleModels() == null ? other.getRuleModels() == null : this.getRuleModels().equals(other.getRuleModels()))
            && (this.getSort() == null ? other.getSort() == null : this.getSort().equals(other.getSort()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getStrategyId() == null) ? 0 : getStrategyId().hashCode());
        result = prime * result + ((getAwardId() == null) ? 0 : getAwardId().hashCode());
        result = prime * result + ((getAwardTitle() == null) ? 0 : getAwardTitle().hashCode());
        result = prime * result + ((getAwardSubtitle() == null) ? 0 : getAwardSubtitle().hashCode());
        result = prime * result + ((getAwardCount() == null) ? 0 : getAwardCount().hashCode());
        result = prime * result + ((getAwardCountSurplus() == null) ? 0 : getAwardCountSurplus().hashCode());
        result = prime * result + ((getAwardRate() == null) ? 0 : getAwardRate().hashCode());
        result = prime * result + ((getRuleModels() == null) ? 0 : getRuleModels().hashCode());
        result = prime * result + ((getSort() == null) ? 0 : getSort().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", strategyId=").append(strategyId);
        sb.append(", awardId=").append(awardId);
        sb.append(", awardTitle=").append(awardTitle);
        sb.append(", awardSubtitle=").append(awardSubtitle);
        sb.append(", awardCount=").append(awardCount);
        sb.append(", awardCountSurplus=").append(awardCountSurplus);
        sb.append(", awardRate=").append(awardRate);
        sb.append(", ruleModels=").append(ruleModels);
        sb.append(", sort=").append(sort);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}