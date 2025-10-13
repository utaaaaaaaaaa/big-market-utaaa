package com.uta.api.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetRaffleAwardListVO {

    private Integer awardId;
    private String awardTitle;
    private String awardSubtitle;
    private Integer sort;

    // 奖品是否解锁
    private Boolean isAwardUnlock;
    // 奖品解锁次数规则
    private Integer awardRuleLockCount;
    // 奖品等待解锁次数
    private Integer waitUnlockCount;

}
