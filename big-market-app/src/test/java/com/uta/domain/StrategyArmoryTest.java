package com.uta.domain;

import com.uta.domain.strategy.service.armory.StrategyArmory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class StrategyArmoryTest {

    @Autowired
    private StrategyArmory strategyArmory;

    @Test
    public void testStrategyArmory() {
        Long id = 100001L;
        strategyArmory.assembleLotteryStrategy(id);
        log.info("【抽奖结果】{}", strategyArmory.getRandomAwardId(id));
        log.info("【抽奖结果】{}", strategyArmory.getRandomAwardId(id));
        log.info("【抽奖结果】{}", strategyArmory.getRandomAwardId(id));
        log.info("【抽奖结果】{}", strategyArmory.getRandomAwardId(id));
    }

}
