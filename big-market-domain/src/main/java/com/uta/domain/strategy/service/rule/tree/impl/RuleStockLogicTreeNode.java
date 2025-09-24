package com.uta.domain.strategy.service.rule.tree.impl;

import com.uta.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import com.uta.domain.strategy.model.vo.StrategyAwardStockKeyVO;
import com.uta.domain.strategy.repository.IStrategyRepository;
import com.uta.domain.strategy.service.armory.IStrategyDispatch;
import com.uta.domain.strategy.service.rule.tree.ILogicTreeNode;
import com.uta.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component("rule_stock")
public class RuleStockLogicTreeNode implements ILogicTreeNode {

    @Resource
    private IStrategyDispatch dispatch;

    @Resource
    private IStrategyRepository repository;

    @Override
    public DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Integer awardId, String ruleValue) {
        log.info("【规则过滤】-库存扣减 userId:{} strategyId:{} awardId:{}", userId, strategyId, awardId);

        //扣减库存
        Boolean b = dispatch.subtractionAwardStock(strategyId, awardId);
        //库存扣减成功
        if (b){
            //写入延迟队列，延迟消费更新数据库记录。【在trigger的job：UpdateAwardStockJob 下消费记录，更新数据库信息】
            repository.awardStockConsumeSendMsgQueue(StrategyAwardStockKeyVO.builder()
                    .awardId(awardId)
                    .strategyId(strategyId)
                    .build());

            return DefaultTreeFactory.TreeActionEntity.builder()
                    .ruleLogicCheckType(RuleLogicCheckTypeVO.TAKE_OVER)
                    .strategyAwardVO(DefaultTreeFactory.StrategyAwardVO.builder()
                            .awardId(awardId)
                            .awardRuleValue(ruleValue)
                            .build())
                    .build();
        }

        return DefaultTreeFactory.TreeActionEntity.builder()
                .ruleLogicCheckType(RuleLogicCheckTypeVO.ALLOW)
                .strategyAwardVO(DefaultTreeFactory.StrategyAwardVO.builder()
                        .awardId(awardId)
                        .awardRuleValue(ruleValue)
                        .build())
                .build();
    }
}
