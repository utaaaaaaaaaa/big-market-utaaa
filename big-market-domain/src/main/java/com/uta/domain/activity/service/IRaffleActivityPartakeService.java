package com.uta.domain.activity.service;

import com.uta.domain.activity.model.entity.PartakeRaffleActivityEntity;
import com.uta.domain.activity.model.entity.UserRaffleOrderEntity;

/**
 * 抽奖活动参与服务
 */
public interface IRaffleActivityPartakeService {

    /**
     * 创建抽奖订单
     *
     * @param partakeRaffleActivity 参与抽奖活动实体对象
     */
    UserRaffleOrderEntity createOrder(PartakeRaffleActivityEntity partakeRaffleActivity);

}
