package com.uta.domain;

import com.uta.domain.strategy.service.armory.StrategyArmoryDispatch;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class StrategyArmoryTest {

    @Autowired
    private StrategyArmoryDispatch strategyArmory;

    @Test
    public void testStrategyArmory() {
        Long id = 100001L;
        strategyArmory.assembleLotteryStrategy(id);
        log.info("【抽奖结果】{}", strategyArmory.getRandomAwardId(id));
        log.info("【抽奖结果】{}", strategyArmory.getRandomAwardId(id));
        log.info("【抽奖结果】{}", strategyArmory.getRandomAwardId(id));
        log.info("【抽奖结果】{}", strategyArmory.getRandomAwardId(id));
    }

    @Test
    public void testStrategyArmoryWithRuleWeight() {
        Long id = 100001L;
//        strategyArmory.assembleLotteryStrategy(id);
        log.info("【4000策略抽奖结果】{}", strategyArmory.getRandomAwardId(id,"4000:102,103,104,105"));
        log.info("【5000策略抽奖结果】{}", strategyArmory.getRandomAwardId(id,"5000:102,103,104,105,106,107"));
        log.info("【6000策略抽奖结果】{}", strategyArmory.getRandomAwardId(id,"6000:102,103,104,105,106,107,108,109"));
        log.info("【4000策略抽奖结果】{}", strategyArmory.getRandomAwardId(id,"4000:102,103,104,105"));
        log.info("【5000策略抽奖结果】{}", strategyArmory.getRandomAwardId(id,"5000:102,103,104,105,106,107"));
        log.info("【6000策略抽奖结果】{}", strategyArmory.getRandomAwardId(id,"6000:102,103,104,105,106,107,108,109"));
        log.info("【4000策略抽奖结果】{}", strategyArmory.getRandomAwardId(id,"4000:102,103,104,105"));
        log.info("【5000策略抽奖结果】{}", strategyArmory.getRandomAwardId(id,"5000:102,103,104,105,106,107"));
        log.info("【6000策略抽奖结果】{}", strategyArmory.getRandomAwardId(id,"6000:102,103,104,105,106,107,108,109"));
        log.info("【4000策略抽奖结果】{}", strategyArmory.getRandomAwardId(id,"4000:102,103,104,105"));
        log.info("【5000策略抽奖结果】{}", strategyArmory.getRandomAwardId(id,"5000:102,103,104,105,106,107"));
        log.info("【6000策略抽奖结果】{}", strategyArmory.getRandomAwardId(id,"6000:102,103,104,105,106,107,108,109"));
        log.info("【4000策略抽奖结果】{}", strategyArmory.getRandomAwardId(id,"4000:102,103,104,105"));
        log.info("【5000策略抽奖结果】{}", strategyArmory.getRandomAwardId(id,"5000:102,103,104,105,106,107"));
        log.info("【6000策略抽奖结果】{}", strategyArmory.getRandomAwardId(id,"6000:102,103,104,105,106,107,108,109"));
    }

}
