package com.uta.domain.activity.service.quota.policy;

import com.uta.domain.activity.model.aggregate.CreateQuotaOrderAggregate;

public interface ITradePolicy {

    void trade (CreateQuotaOrderAggregate createQuotaOrderAggregate);

}
