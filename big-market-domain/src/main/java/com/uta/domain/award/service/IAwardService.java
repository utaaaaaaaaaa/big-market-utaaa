package com.uta.domain.award.service;

import com.uta.domain.award.model.entity.UserAwardRecordEntity;

/**
 * 用户中奖
 */
public interface IAwardService {

    void saveUserAwardRecord(UserAwardRecordEntity userAwardRecordEntity);

}
