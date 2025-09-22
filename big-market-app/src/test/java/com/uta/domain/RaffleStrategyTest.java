package com.uta.domain;

import com.uta.domain.strategy.model.entity.RaffleAwardEntity;
import com.uta.domain.strategy.model.entity.RaffleFactorEntity;
import com.uta.domain.strategy.service.IRaffleStrategy;
import com.uta.domain.strategy.service.rule.impl.RuleWeightLogicFilter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
@Slf4j
public class RaffleStrategyTest {

    @Autowired
    private IRaffleStrategy raffleStrategy;

//    @BeforeEach
//    public void init() {
//        ReflectionTestUtils.setField(RuleWeightLogicFilter.class,"userScore",4500L);
//    }

    @Test
    public void testPerformRaffle() {
        RaffleFactorEntity factor = RaffleFactorEntity.builder()
                .strategyId(100001L)
                .userId("utaaa")
                .build();

        RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(factor);
        log.info("请求参数：{}", factor.toString());
        log.info("请求结果：awardId={}", raffleAwardEntity.getAwardId());
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

}
