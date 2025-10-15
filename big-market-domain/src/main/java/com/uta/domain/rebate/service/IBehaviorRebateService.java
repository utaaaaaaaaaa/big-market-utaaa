package com.uta.domain.rebate.service;

import com.uta.domain.rebate.model.entity.BehaviorEntity;
import com.uta.domain.rebate.model.entity.BehaviorRebateOrderEntity;

import java.util.List;

public interface IBehaviorRebateService {

    /**
     * 创建订单
     * @param behaviorEntity 用户行为实体
     * @return 返回订单号（一个行为可能会创建多个订单）
     */
    List<String> createOrder(BehaviorEntity behaviorEntity);

    /**
     * 根据业务唯一id查询订单
     * @param userId 用户id
     * @param outBusinessNo 业务唯一id
     * @return 订单列表
     */
    List<BehaviorRebateOrderEntity> queryOrderByOutBusinessNo(String userId, String outBusinessNo);

}
