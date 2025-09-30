package com.uta.domain.award;

import com.uta.domain.award.model.entity.UserAwardRecordEntity;
import com.uta.domain.award.model.vo.AwardStateVO;
import com.uta.domain.award.service.IAwardService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

@SpringBootTest
@Slf4j
public class AwardServiceTest {

    @Resource
    private IAwardService awardService;

    /**
     * 模拟发放抽奖记录，流程中会发送MQ，以及接收MQ消息，还有 task 表，补偿发送MQ
     */
    @Test
    public void test_saveUserAwardRecord() throws InterruptedException {
        for (int i = 0; i < 1; i++) {
            UserAwardRecordEntity userAwardRecordEntity = new UserAwardRecordEntity();
            userAwardRecordEntity.setUserId("utaaa");
            userAwardRecordEntity.setActivityId(100301L);
            userAwardRecordEntity.setStrategyId(100006L);
            userAwardRecordEntity.setOrderId(RandomStringUtils.randomNumeric(12));
            userAwardRecordEntity.setAwardId(101);
            userAwardRecordEntity.setAwardTitle("OpenAI 增加使用次数");
            userAwardRecordEntity.setAwardTime(new Date());
            userAwardRecordEntity.setAwardState(AwardStateVO.create);
            awardService.saveUserAwardRecord(userAwardRecordEntity);
            Thread.sleep(500);
        }

        new CountDownLatch(1).await();
    }


}
