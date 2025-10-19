package com.uta.api.entity.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 活动抽奖请求体
 */
@Data
public class ActivityDrawDTO implements Serializable {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 活动ID
     */
    private Long activityId;


}
