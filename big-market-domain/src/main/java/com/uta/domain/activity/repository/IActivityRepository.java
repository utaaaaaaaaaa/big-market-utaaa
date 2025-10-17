package com.uta.domain.activity.repository;

import com.uta.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import com.uta.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import com.uta.domain.activity.model.entity.*;
import com.uta.domain.activity.model.vo.ActivitySkuStockKeyVO;

import java.util.Date;
import java.util.List;

public interface IActivityRepository {

    ActivitySkuEntity queryActivitySku(Long sku);

    ActivityEntity queryRaffleActivityByActivityId(Long activityId);

    ActivityCountEntity queryRaffleActivityCountByActivityCountId(Long activityCountId);

    void doSaveNoPayOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate);

    void doSaveCreditPayOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate);

    void cacheActivitySkuStockCount(String cacheKey, Integer stockCount);

    boolean subtractionActivitySkuStock(Long sku, String cacheKey, Date endDateTime);

    void activitySkuStockConsumeSendQueue(ActivitySkuStockKeyVO build);

    ActivitySkuStockKeyVO takeQueueValue();

    void clearQueueValue();

    void updateActivitySkuStock(Long sku);

    void clearActivitySkuStock(Long sku);

    void saveCreatePartakeOrderAggregate(CreatePartakeOrderAggregate createPartakeOrderAggregate);

    ActivityAccountEntity queryActivityAccountByUserId(String userId, Long activityId);

    ActivityAccountMonthEntity queryActivityAccountMonthByUserId(String userId, Long activityId, String month);

    ActivityAccountDayEntity queryActivityAccountDayByUserId(String userId, Long activityId, String day);

    UserRaffleOrderEntity queryNoUsedRaffleOrder(PartakeRaffleActivityEntity partakeRaffleActivity);

    List<ActivitySkuEntity> queryActivitySkuListByActivityId(Long activityId);

    Integer queryUserDayPartakeCount(Long activityId, String userId);

    ActivityAccountEntity getRaffleActivityAccount(String userId, Long activityId);

    Integer queryRaffleActivityAccountPartakeCount(Long activityId, String userId);

    void updateOrder(DeliveryOrderEntity deliveryOrderEntity);

    UnpaidActivityOrderEntity queryUnpaidActivityOrder(SkuRechargeEntity skuRechargeEntity);

    List<SkuProductEntity> querySkuProductEntityListByActivityId(Long activityId);
}

