package com.uta.trigger;

import com.alibaba.fastjson.JSON;
import com.uta.api.IRaffleStrategyService;
import com.uta.api.entity.dto.GetRaffleAwardListDTO;
import com.uta.api.entity.vo.GetRaffleAwardListVO;
import com.uta.types.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@SpringBootTest
public class RaffleStrategyControllerTest {

    @Resource
    private IRaffleStrategyService raffleStrategyService;

    @Test
    public void test_queryRaffleAwardList() {
        GetRaffleAwardListDTO request = new GetRaffleAwardListDTO();
        request.setUserId("utaaa");
        request.setActivityId(100301L);
        Response<List<GetRaffleAwardListVO>> response = raffleStrategyService.getRaffleAwardList(request);

        log.info("请求参数：{}", JSON.toJSONString(request));
        log.info("测试结果：{}", JSON.toJSONString(response));
    }

}

