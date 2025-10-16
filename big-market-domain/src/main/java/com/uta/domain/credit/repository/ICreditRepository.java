package com.uta.domain.credit.repository;

import com.uta.domain.credit.model.aggregate.TradeAggregate;

public interface ICreditRepository {

    void saveUserCreditTradeOrder(TradeAggregate tradeAggregate);

}
