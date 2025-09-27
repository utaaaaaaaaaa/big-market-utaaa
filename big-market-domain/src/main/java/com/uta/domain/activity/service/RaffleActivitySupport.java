package com.uta.domain.activity.service;

import com.uta.domain.activity.model.entity.ActivityCountEntity;
import com.uta.domain.activity.model.entity.ActivityEntity;
import com.uta.domain.activity.model.entity.ActivitySkuEntity;
import com.uta.domain.activity.repository.IActivityRepository;
import com.uta.domain.activity.service.rule.factory.DefaultActivityChainFactory;

/**
 * @description 抽奖活动支撑类
 */
public class RaffleActivitySupport {

    protected DefaultActivityChainFactory defaultActivityChainFactory;

    protected IActivityRepository activityRepository;

    public RaffleActivitySupport(DefaultActivityChainFactory defaultActivityChainFactory, IActivityRepository activityRepository) {
        this.defaultActivityChainFactory = defaultActivityChainFactory;
        this.activityRepository = activityRepository;
    }


    public ActivitySkuEntity getActivitySkuEntity(Long sku) {
        return activityRepository.queryActivitySku(sku);
    }

    public ActivityEntity getActivityEntityByActivityId(Long activityId) {
        return activityRepository.queryRaffleActivityByActivityId(activityId);
    }

    public ActivityCountEntity getActivityCountEntityByActivityCountId(Long activityCountId) {
        return activityRepository.queryRaffleActivityCountByActivityCountId(activityCountId);
    }

}
