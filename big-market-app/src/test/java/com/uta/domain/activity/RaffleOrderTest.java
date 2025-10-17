package com.uta.domain.activity;

import com.uta.domain.activity.model.entity.SkuRechargeEntity;
import com.uta.domain.activity.model.vo.OrderTradeTypeVO;
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
    private IRaffleActivityAccountQuotaService raffleActivityAccountQuotaService;

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
        skuRechargeEntity.setOrderTradeType(OrderTradeTypeVO.rebate_no_pay_trade);
        skuRechargeEntity.setOutBusinessNo("400028219012");

        String orderId = raffleActivityAccountQuotaService.createOrder(skuRechargeEntity).getOrderId();
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
                skuRechargeEntity.setOrderTradeType(OrderTradeTypeVO.rebate_no_pay_trade);

                String orderId = raffleActivityAccountQuotaService.createOrder(skuRechargeEntity).getOrderId();
                log.info("测试结果：{}", orderId);
            }catch (AppException e){
                log.warn(e.getInfo());
            }
        }

        new CountDownLatch(1).await();
    }

    @Test
    public void test_credit_pay_trade() {
        SkuRechargeEntity skuRechargeEntity = new SkuRechargeEntity();
        skuRechargeEntity.setUserId("utaaa");
        skuRechargeEntity.setSku(9011L);
        // outBusinessNo 作为幂等仿重使用，同一个业务单号2次使用会抛出索引冲突 Duplicate entry '700091009111' for key 'uq_out_business_no' 确保唯一性。
        skuRechargeEntity.setOutBusinessNo("70009240608007");
        skuRechargeEntity.setOrderTradeType(OrderTradeTypeVO.credit_pay_trade);
        String orderId = raffleActivityAccountQuotaService.createOrder(skuRechargeEntity).getOrderId();
        log.info("测试结果：{}", orderId);
    }

}
