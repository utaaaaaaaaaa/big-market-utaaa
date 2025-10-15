package com.uta.domain.rebate.repository;

import com.uta.domain.rebate.model.aggregate.BehaviorRebateAggregate;
import com.uta.domain.rebate.model.entity.BehaviorRebateOrderEntity;
import com.uta.domain.rebate.model.vo.BehaviorTypeVO;
import com.uta.domain.rebate.model.vo.DailyBehaviorRebateVO;

import java.util.List;

public interface IBehaviorRebateRepository {

    List<DailyBehaviorRebateVO> queryDailyBehaviorRebateConfig(BehaviorTypeVO behaviorTypeVO);

    void saveUserRebateRecord(String userId, List<BehaviorRebateAggregate> behaviorRebateAggregates);

    List<BehaviorRebateOrderEntity> queryOrderByOutBusinessNo(String userId, String outBusinessNo);
}
