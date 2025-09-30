package com.uta.trigger.job;

import com.uta.domain.strategy.model.vo.StrategyAwardStockKeyVO;
import com.uta.domain.strategy.service.IRaffleStock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @description 更新奖品库存任务；redis缓存库存，异步队列更新数据库，数据库表最终一致即可
 */
@Slf4j
@Component
public class UpdateAwardStockJob {

    @Resource
    private IRaffleStock raffleStock;

    @Scheduled(cron = "0/5 * * * * ?")
    public void exec() {
        try {
            StrategyAwardStockKeyVO strategyAwardStockKeyVO = raffleStock.takeQueueValue();
            if (strategyAwardStockKeyVO == null) {return;}
            log.info("【定时任务】-更新奖品消耗库存 strategyId:{}, awardId:{}", strategyAwardStockKeyVO.getStrategyId(), strategyAwardStockKeyVO.getAwardId());
            raffleStock.updateStrategyAwardStock(strategyAwardStockKeyVO.getStrategyId(), strategyAwardStockKeyVO.getAwardId());
        } catch (InterruptedException e) {
            log.error("【定时任务】-更新奖品消耗库存失败", e);
        }
    }

}
