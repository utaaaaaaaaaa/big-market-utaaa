package com.uta.domain.award.service;

import com.uta.domain.award.model.entity.DistributeAwardEntity;
import com.uta.domain.award.model.entity.UserAwardRecordEntity;

/**
 * 用户中奖
 */
public interface IAwardService {

    void saveUserAwardRecord(UserAwardRecordEntity userAwardRecordEntity);

    /**
     * 配送发货奖品
     */
    void distributeAward(DistributeAwardEntity distributeAwardEntity);

}
