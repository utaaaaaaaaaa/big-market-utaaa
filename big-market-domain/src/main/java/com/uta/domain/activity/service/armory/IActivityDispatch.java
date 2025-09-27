package com.uta.domain.activity.service.armory;

import java.util.Date;

/**
 * 活动调度（扣减库存）
 */
public interface IActivityDispatch {

    /**
     * 扣减奖品缓存库存（这里的奖品应该对应的是活动赠送的次数或者奖品，而不是之前抽奖的奖品）
     *
     * @param sku sku
     * @param endDateTime 活动结束日期，根据对应日期设置缓存key的结束时间
     * @return 扣减结果
     */
    boolean subtractionActivitySkuStock(Long sku, Date endDateTime);

}
