package com.uta.domain;

import com.uta.domain.strategy.model.entity.RaffleAwardEntity;
import com.uta.domain.strategy.model.entity.RaffleFactorEntity;
import com.uta.domain.strategy.service.IRaffleStrategy;
import com.uta.domain.strategy.service.armory.IStrategyArmory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;

@SpringBootTest
@Slf4j
public class RaffleStrategyTest {

    @Autowired
    private IRaffleStrategy raffleStrategy;

    @Autowired
    private IStrategyArmory strategyArmory;

//    @BeforeEach
//    public void init() {
//        log.info("测试结果：{}", strategyArmory.assembleLotteryStrategy(100001L));
//        log.info("测试结果：{}", strategyArmory.assembleLotteryStrategy(100002L));
//    }

    @Test
    public void testPerformRaffle() throws InterruptedException {
        log.info("测试结果：{}", strategyArmory.assembleLotteryStrategy(100006L));
        RaffleFactorEntity factor = RaffleFactorEntity.builder()
                .strategyId(100006L)
                .userId("utaaa")
                .build();

        for (int i = 0; i < 1; i++) {
            RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(factor);
            log.info("请求参数：{}", factor.toString());
            log.info("请求结果：awardId={}", raffleAwardEntity.getAwardId());
        }

        new CountDownLatch(1).await();
    }

    @Test
    public void testPerformRaffleBlackList() {
        RaffleFactorEntity factor = RaffleFactorEntity.builder()
                .strategyId(100001L)
                .userId("user001")
                .build();

        RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(factor);
        log.info("请求参数：{}", factor.toString());
        log.info("请求结果：awardId={}", raffleAwardEntity.getAwardId());
    }

    @Test
    public void testPerformRaffleMidRuleLock() {
        RaffleFactorEntity factor = RaffleFactorEntity.builder()
                .strategyId(100003L)
                .userId("utaaa")
                .build();

        RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(factor);

        log.info("请求参数：{}", factor.toString());
        log.info("请求结果：awardId={}", raffleAwardEntity.toString());
    }

}
