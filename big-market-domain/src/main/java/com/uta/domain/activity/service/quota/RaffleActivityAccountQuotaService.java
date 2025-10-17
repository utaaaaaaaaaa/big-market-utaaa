package com.uta.domain.activity.service.quota;

import com.uta.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import com.uta.domain.activity.model.entity.*;
import com.uta.domain.activity.model.vo.ActivitySkuStockKeyVO;
import com.uta.domain.activity.repository.IActivityRepository;
import com.uta.domain.activity.service.IRaffleActivitySkuStockService;
import com.uta.domain.activity.service.quota.policy.ITradePolicy;
import com.uta.domain.activity.service.quota.rule.factory.DefaultActivityChainFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class RaffleActivityAccountQuotaService extends AbstractRaffleActivityAccountQuota implements IRaffleActivitySkuStockService {

    public RaffleActivityAccountQuotaService(DefaultActivityChainFactory defaultActivityChainFactory, IActivityRepository activityRepository, Map<String, ITradePolicy> tradePolicyMap) {
        super(defaultActivityChainFactory, activityRepository, tradePolicyMap);
    }

    @Override
    protected CreateQuotaOrderAggregate buildOrderAggregate(SkuRechargeEntity skuRechargeEntity, ActivityEntity activityEntity, ActivitySkuEntity activitySkuEntity, ActivityCountEntity activityCountEntity) {
        // 订单实体对象
        ActivityOrderEntity activityOrderEntity = new ActivityOrderEntity();
        activityOrderEntity.setUserId(skuRechargeEntity.getUserId());
        activityOrderEntity.setSku(skuRechargeEntity.getSku());
        activityOrderEntity.setActivityId(activityEntity.getActivityId());
        activityOrderEntity.setActivityName(activityEntity.getActivityName());
        activityOrderEntity.setStrategyId(activityEntity.getStrategyId());
        // 公司里一般会有专门的雪花算法UUID服务，我们这里直接生成个12位就可以了。
        activityOrderEntity.setOrderId(RandomStringUtils.randomNumeric(12));
        activityOrderEntity.setOrderTime(new Date());
        activityOrderEntity.setTotalCount(activityCountEntity.getTotalCount());
        activityOrderEntity.setDayCount(activityCountEntity.getDayCount());
        activityOrderEntity.setMonthCount(activityCountEntity.getMonthCount());
        activityOrderEntity.setPayAmount(activitySkuEntity.getProductAmount());
        activityOrderEntity.setOutBusinessNo(skuRechargeEntity.getOutBusinessNo());

        // 构建聚合对象
        return CreateQuotaOrderAggregate.builder()
                .userId(skuRechargeEntity.getUserId())
                .activityId(activitySkuEntity.getActivityId())
                .totalCount(activityCountEntity.getTotalCount())
                .dayCount(activityCountEntity.getDayCount())
                .monthCount(activityCountEntity.getMonthCount())
                .activityOrderEntity(activityOrderEntity)
                .build();
    }

    @Override
    protected void doSaveOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate) {
        activityRepository.doSaveNoPayOrder(createQuotaOrderAggregate);
    }

    @Override
    public ActivitySkuStockKeyVO takeQueueValue() throws InterruptedException {
        return activityRepository.takeQueueValue();
    }

    @Override
    public void clearQueueValue() {
        activityRepository.clearQueueValue();
    }

    @Override
    public void updateActivitySkuStock(Long sku) {
        activityRepository.updateActivitySkuStock(sku);
    }

    @Override
    public void clearActivitySkuStock(Long sku) {
        activityRepository.clearActivitySkuStock(sku);
    }

    @Override
    public void updateOrder(DeliveryOrderEntity deliveryOrderEntity) {
        activityRepository.updateOrder(deliveryOrderEntity);
    }

    @Override
    public Integer getUserDayPartakeCount(String userId, Long activityId) {
        return activityRepository.queryUserDayPartakeCount(activityId, userId);
    }

    @Override
    public ActivityAccountEntity getRaffleActivityAccount(String userId, Long activityId) {
        return activityRepository.getRaffleActivityAccount(userId, activityId);
    }

    @Override
    public Integer queryRaffleActivityAccountPartakeCount(Long activityId, String userId) {
        return activityRepository.queryRaffleActivityAccountPartakeCount(activityId, userId);
    }
}
