package com.uta.infrastructure;

import com.uta.infrastructure.persistent.dao.RaffleActivityMapper;
import com.uta.infrastructure.persistent.dao.RaffleActivityOrderMapper;
import com.uta.infrastructure.persistent.po.RaffleActivity;
import com.uta.infrastructure.persistent.po.RaffleActivityOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@SpringBootTest
public class RaffleActivityTest {

    @Resource
    private RaffleActivityOrderMapper raffleActivityOrderMapper;

//    @Test
//    public void testInsert(){
//        RaffleActivityOrder raffleActivityOrder = new RaffleActivityOrder();
//        raffleActivityOrder.setUserId("utaaa");
//        raffleActivityOrder.setSku(9011L);
//        raffleActivityOrder.setActivityId(100301L);
//        raffleActivityOrder.setActivityName("测试活动");
//        raffleActivityOrder.setTotalCount(0);
//        raffleActivityOrder.setDayCount(0);
//        raffleActivityOrder.setMonthCount(0);
//        raffleActivityOrder.setStrategyId(100006L);
//        raffleActivityOrder.setOrderId(RandomStringUtils.randomNumeric(12));
//        raffleActivityOrder.setOutBusinessNo(RandomStringUtils.randomNumeric(11));
//        raffleActivityOrder.setOrderTime(new Date());
//        raffleActivityOrder.setState("not_used");
//
//        raffleActivityOrderMapper.insert(raffleActivityOrder);
//    }

    @Test
    public void testRaffleActivityOrder() {
        List<RaffleActivityOrder> order = raffleActivityOrderMapper.queryRaffleActivityOrderByUserId("utaaa");
        log.info("raffleActivityOrder:{}",order.toString());
    }

}
