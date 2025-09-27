package com.uta.domain.activity.service.rule;

import com.uta.domain.activity.model.entity.ActivityCountEntity;
import com.uta.domain.activity.model.entity.ActivityEntity;
import com.uta.domain.activity.model.entity.ActivitySkuEntity;

public interface IActionChain extends IActionChainArmory{

    boolean action(ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity);

}
