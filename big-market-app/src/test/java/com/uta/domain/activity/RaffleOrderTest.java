package com.uta.domain.activity;

import com.uta.domain.activity.model.entity.SkuRechargeEntity;
import com.uta.domain.activity.service.IRaffleActivityAccountQuotaService;
import com.uta.domain.activity.service.armory.IActivityArmory;
import com.uta.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;

@Slf4j
@SpringBootTest
public class RaffleOrderTest {

    @Resource
    private IRaffleActivityAccountQuotaService raffleOrder;

    @Resource
    private IActivityArmory activityArmory;

    @BeforeEach
    public void init() {
        log.info("装配活动：{}", activityArmory.assembleActivitySku(9011L));
    }

    @Test
    public void testCreateOrderDuplicate() {
        SkuRechargeEntity skuRechargeEntity = new SkuRechargeEntity();
        skuRechargeEntity.setUserId("utaaa");
        skuRechargeEntity.setSku(9011L);
        skuRechargeEntity.setOutBusinessNo("400028219012");

        String orderId = raffleOrder.createOrder(skuRechargeEntity);
        log.info("测试结果：{}", orderId);
    }

    @Test
    public void testCreateOrder() throws InterruptedException {
        for (int i = 0; i < 20; i++) {
            try {
                SkuRechargeEntity skuRechargeEntity = new SkuRechargeEntity();
                skuRechargeEntity.setUserId("utaaa");
                skuRechargeEntity.setSku(9011L);
                skuRechargeEntity.setOutBusinessNo(RandomStringUtils.randomNumeric(12));

                String orderId = raffleOrder.createOrder(skuRechargeEntity);
                log.info("测试结果：{}", orderId);
            }catch (AppException e){
                log.warn(e.getInfo());
            }
        }

        new CountDownLatch(1).await();
    }
    
}
