package com.uta.domain.activity.service;

import com.uta.domain.activity.model.vo.ActivitySkuStockKeyVO;

/**
 * 活动库存处理接口
 */
public interface ISkuStock {

    /**
     * 获取sku库存消耗队列
     * @return 奖品库存sku消息
     * @throws InterruptedException
     */
    ActivitySkuStockKeyVO takeQueueValue() throws InterruptedException;

    /**
     * 接收MQ消息后清空队列
     */
    void clearQueueValue();

    /**
     * 延迟队列 + 任务驱使更新sku库存
     * @param sku 活动商品
     */
    void updateActivitySkuStock(Long sku);

    /**
     * 清空数据库库存
     * @param sku 活动商品
     */
    void clearActivitySkuStock(Long sku);

}
