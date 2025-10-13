package com.uta.domain.rebate.model.entity;

import com.uta.domain.rebate.model.vo.BehaviorTypeVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 行为实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BehaviorEntity {

    // 用户id
    private String userId;
    // 用户行为类型
    private BehaviorTypeVO behaviorTypeVO;
    // 唯一业务id（透传）
    private String outBusinessNo;

}
