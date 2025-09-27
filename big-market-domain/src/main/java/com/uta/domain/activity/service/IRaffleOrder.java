package com.uta.domain.activity.service;

import com.uta.domain.activity.model.entity.ActivityOrderEntity;
import com.uta.domain.activity.model.entity.ActivityShopCartEntity;
import com.uta.domain.activity.model.entity.SkuRechargeEntity;

/**
 * @description 抽奖活动订单接口
 */
public interface IRaffleOrder {

    /**
     * 以sku创建抽奖活动订单，获得参与抽奖资格（可消耗的次数）
     *
     * @param activityShopCartEntity 活动sku实体，通过sku领取活动。
     * @return 活动参与记录实体
     */
    ActivityOrderEntity createRaffleActivityOrder(ActivityShopCartEntity activityShopCartEntity);

    /***
     * 创建sku充值订单
     *
     * @param skuRechargeEntity 活动sku充值对象实体
     * @return 定单号
     */
    String createSkuRechargeOrder(SkuRechargeEntity skuRechargeEntity);

}
