package com.uta.domain.activity.service.rule.factory;

import com.uta.domain.activity.service.rule.IActionChain;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 责任链工厂
 */
@Service
public class DefaultActivityChainFactory {

    private final IActionChain actionChain;

    public DefaultActivityChainFactory(Map<String, IActionChain> actionChainMap) {
        actionChain = actionChainMap.get(ActionModel.activity_base_action.code);
        actionChain.appendNext(actionChainMap.get(ActionModel.activity_sku_stock_action.code));
    }

    public IActionChain openActionChain() {
        return this.actionChain;
    }

    @Getter
    @AllArgsConstructor
    public enum ActionModel{

        activity_base_action("activity_base_action","活动时间、状态校验"),
        activity_sku_stock_action("activity_sku_stock_action","活动sku库存")
        ;

        private final String code;
        private final String info;

    }

}
