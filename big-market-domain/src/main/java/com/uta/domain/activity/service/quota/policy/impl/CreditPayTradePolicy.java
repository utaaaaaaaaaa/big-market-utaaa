package com.uta.domain.activity.service.quota.policy.impl;

import com.uta.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import com.uta.domain.activity.model.vo.OrderStateVO;
import com.uta.domain.activity.repository.IActivityRepository;
import com.uta.domain.activity.service.quota.policy.ITradePolicy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("credit_pay_trade")
public class CreditPayTradePolicy implements ITradePolicy {

    private final IActivityRepository activityRepository;

    public CreditPayTradePolicy(IActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Override
    public void trade(CreateQuotaOrderAggregate createQuotaOrderAggregate) {
        createQuotaOrderAggregate.setOrderState(OrderStateVO.wait_pay);
        activityRepository.doSaveCreditPayOrder(createQuotaOrderAggregate);
    }
}
