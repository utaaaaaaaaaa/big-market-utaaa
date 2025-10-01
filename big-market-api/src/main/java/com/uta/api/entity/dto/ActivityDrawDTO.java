package com.uta.api.entity.dto;

import lombok.Data;

/**
 * 活动抽奖请求体
 */
@Data
public class ActivityDrawDTO {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 活动ID
     */
    private Long activityId;


}
