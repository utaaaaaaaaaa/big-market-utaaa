package com.uta.domain.award.repository;

import com.uta.domain.award.model.aggregate.UserAwardRecordAggregate;
import org.springframework.stereotype.Repository;

/**
 * 奖品仓储
 */
public interface IAwardRepository {

    void saveUserAwardRecord(UserAwardRecordAggregate userAwardRecordAggregate);
}
