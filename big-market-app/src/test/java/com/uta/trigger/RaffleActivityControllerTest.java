package com.uta.trigger;

import com.alibaba.fastjson.JSON;
import com.uta.api.IRaffleActivityService;
import com.uta.api.entity.dto.ActivityDrawDTO;
import com.uta.api.entity.vo.ActivityDrawVO;
import com.uta.types.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

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


}
