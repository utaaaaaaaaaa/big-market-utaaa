package com.uta.domain.rebate;

import com.alibaba.fastjson.JSON;
import com.uta.domain.rebate.model.entity.BehaviorEntity;
import com.uta.domain.rebate.model.vo.BehaviorTypeVO;
import com.uta.domain.rebate.service.BehaviorRebateService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest
public class BehaviorRebateServiceTest {

    @Autowired
    private BehaviorRebateService rebateService;

    @Test
    public void test(){
        BehaviorEntity behaviorEntity = BehaviorEntity.builder()
                .userId("utaaa")
                .behaviorTypeVO(BehaviorTypeVO.SIGN)
                .outBusinessNo("20251014")
                .build();
        List<String> orderIds = rebateService.createOrder(behaviorEntity);
        log.info("请求参数：{}", JSON.toJSONString(behaviorEntity));
        log.info("请求结果：{}", JSON.toJSONString(orderIds));
    }

}
