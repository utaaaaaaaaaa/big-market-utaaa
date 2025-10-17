package com.uta.trigger;

import com.alibaba.fastjson.JSON;
import com.uta.api.IRaffleActivityService;
import com.uta.api.entity.dto.ActivityDrawDTO;
import com.uta.api.entity.dto.CreditPayExchangeSkuDTO;
import com.uta.api.entity.vo.ActivityDrawVO;
import com.uta.api.entity.vo.SkuProductListVO;
import com.uta.types.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Slf4j
@SpringBootTest
public class RaffleActivityControllerTest {

    @Resource
    private IRaffleActivityService raffleActivityService;

    @Test
    public void test_armory() {
        Response<Boolean> response = raffleActivityService.armory(100301L);
        log.info("测试结果：{}", JSON.toJSONString(response));
    }

    @Test
    public void test_draw() {
        ActivityDrawDTO request = new ActivityDrawDTO();
        request.setActivityId(100301L);
        request.setUserId("utaaa");
        Response<ActivityDrawVO> response = raffleActivityService.draw(request);

        log.info("请求参数：{}", JSON.toJSONString(request));
        log.info("测试结果：{}", JSON.toJSONString(response));
    }

    @Test
    public void test_draw_blacklist() throws InterruptedException {
        ActivityDrawDTO request = new ActivityDrawDTO();
        request.setActivityId(100301L);
        request.setUserId("user003");
        Response<ActivityDrawVO> response = raffleActivityService.draw(request);

        log.info("请求参数：{}", JSON.toJSONString(request));
        log.info("测试结果：{}", JSON.toJSONString(response));

        new CountDownLatch(1).await();
    }

    @Test
    public void test_calendarSign(){
        Response<Boolean> response = raffleActivityService.calendarSignRebate("user003");
        log.info("测试结果：{}", JSON.toJSONString(response));
    }

    @Test
    public void test_querySkuProductListByActivityId() {
        Long request = 100301L;
        Response<List<SkuProductListVO>> response = raffleActivityService.querySkuProductListByActivityId(request);
        log.info("请求参数：{}", JSON.toJSONString(request));
        log.info("测试结果：{}", JSON.toJSONString(response));
    }

    @Test
    public void test_queryUserCreditAccount() {
        String request = "utaaa";
        Response<BigDecimal> response = raffleActivityService.queryUserCreditAccount(request);
        log.info("请求参数：{}", JSON.toJSONString(request));
        log.info("测试结果：{}", JSON.toJSONString(response));
    }

    @Test
    public void test_creditPayExchangeSku() throws InterruptedException {
        CreditPayExchangeSkuDTO request = new CreditPayExchangeSkuDTO();
        request.setUserId("utaaa");
        request.setSku(9011L);
        Response<Boolean> response = raffleActivityService.creditPayExchangeSku(request);
        log.info("请求参数：{}", JSON.toJSONString(request));
        log.info("测试结果：{}", JSON.toJSONString(response));
        new CountDownLatch(1).await();
    }

}
