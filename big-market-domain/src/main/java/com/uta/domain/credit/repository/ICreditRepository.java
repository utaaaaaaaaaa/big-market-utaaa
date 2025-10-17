package com.uta.domain.credit.repository;

import com.uta.domain.credit.model.aggregate.TradeAggregate;
import com.uta.domain.credit.model.entity.CreditAccountEntity;

public interface ICreditRepository {

    void saveUserCreditTradeOrder(TradeAggregate tradeAggregate);

    CreditAccountEntity queryUserCreditAccount(String userId);
}
