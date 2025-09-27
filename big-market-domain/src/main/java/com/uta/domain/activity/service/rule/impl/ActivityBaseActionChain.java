package com.uta.domain.activity.service.rule.impl;

import com.uta.domain.activity.model.entity.ActivityCountEntity;
import com.uta.domain.activity.model.entity.ActivityEntity;
import com.uta.domain.activity.model.entity.ActivitySkuEntity;
import com.uta.domain.activity.model.vo.ActivityStateVO;
import com.uta.domain.activity.service.rule.AbstractActionChain;
import com.uta.types.enums.ResponseCode;
import com.uta.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component("activity_base_action")
public class ActivityBaseActionChain extends AbstractActionChain {

    @Override
    public boolean action(ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity) {
        log.info("活动责任链-基础信息（有效期、状态、sku库存）校验开始。sku:{} activityId:{}",activitySkuEntity.getSku(),activitySkuEntity.getActivityId());
        // 校验活动状态
        if (!activityEntity.getState().equals(ActivityStateVO.open)){
            log.error("当前活动未开启，activityId:{}, state:{}",activitySkuEntity.getActivityId(),activityEntity.getState().getCode());
            throw new AppException(ResponseCode.ACTIVITY_STATE_ERROR.getCode(), ResponseCode.ACTIVITY_STATE_ERROR.getInfo());
        }

        // 校验活动时间
        Date now = new Date();
        if (activityEntity.getBeginDateTime().after(now) || activityEntity.getEndDateTime().before(now)){
            log.error("现在不是活动时间，now:{}, beginDateTime:{}, endDateTime:{}",now,activityEntity.getBeginDateTime(),activityEntity.getEndDateTime());
            throw new AppException(ResponseCode.ACTIVITY_DATE_ERROR.getCode(), ResponseCode.ACTIVITY_DATE_ERROR.getInfo());
        }

        // 校验sku库存
        if (activitySkuEntity.getStockCountSurplus() <= 0) {
            log.error("sku库存不足，skuStockCountSurplus:{}", activitySkuEntity.getStockCountSurplus());
            throw new AppException(ResponseCode.ACTIVITY_SKU_STOCK_ERROR.getCode(), ResponseCode.ACTIVITY_SKU_STOCK_ERROR.getInfo());
        }

        return next().action(activitySkuEntity, activityEntity, activityCountEntity);
    }
}
