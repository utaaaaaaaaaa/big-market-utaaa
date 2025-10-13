package com.uta.domain.rebate.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 行为类型
 */
@Getter
@AllArgsConstructor
public enum BehaviorTypeVO {

    SIGN("sign","签到（日历）"),
    OPENAI_PAY("openai_pay","openai 外部支付完成"),
    ;

    private final String code;
    private final String info;

}
