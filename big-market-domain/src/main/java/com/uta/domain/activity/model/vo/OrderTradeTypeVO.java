package com.uta.domain.activity.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderTradeTypeVO {

    credit_pay_trade("credit_pay_trade","积分兑换，需要支付交易类"),
    rebate_no_pay_trade("rebate_no_pay_trade","返利奖品，不需要支付交易类")
    ;

    private final String code;
    private final String desc;
}
