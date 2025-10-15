package com.uta.domain.activity.service;

import com.uta.domain.activity.model.entity.ActivityAccountEntity;
import com.uta.domain.activity.model.entity.ActivityOrderEntity;
import com.uta.domain.activity.model.entity.ActivityShopCartEntity;
import com.uta.domain.activity.model.entity.SkuRechargeEntity;

/**
 * @description 抽奖活动用户额度订单接口
 */
public interface IRaffleActivityAccountQuotaService {

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
    String createOrder(SkuRechargeEntity skuRechargeEntity);

    /***
     * 查询用户今日参与抽奖次数
     *
     * @param userId 用户Id
     * @param activityId 活动Id
     * @return 今日抽奖次数
     */
    Integer getUserDayPartakeCount(String userId, Long activityId);

    /**
     * 查询活动账户实体
     *
     * @param userId 用户id
     * @param activityId 活动id
     * @return 活动账户实体
     */
    ActivityAccountEntity getRaffleActivityAccount(String userId, Long activityId);

    /**
     * 查询用户在一个活动中总抽奖次数
     *
     * @param userId 用户id
     * @param activityId 活动id
     * @return 抽奖总次数
     */
    Integer queryRaffleActivityAccountPartakeCount(Long activityId, String userId);
}
