package com.uta.domain.award.service;

import com.uta.domain.award.event.SendAwardMessageEvent;
import com.uta.domain.award.model.aggregate.UserAwardRecordAggregate;
import com.uta.domain.award.model.entity.DistributeAwardEntity;
import com.uta.domain.award.model.entity.TaskEntity;
import com.uta.domain.award.model.entity.UserAwardRecordEntity;
import com.uta.domain.award.model.vo.TaskStateVO;
import com.uta.domain.award.repository.IAwardRepository;
import com.uta.domain.award.service.distribute.IDistributeAward;
import com.uta.types.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@Service
public class AwardService implements IAwardService {

    private final Map<String, IDistributeAward> distributeAwardMap;

    @Resource
    private IAwardRepository awardRepository;

    @Resource
    private SendAwardMessageEvent sendAwardMessageEvent;

    public AwardService(Map<String, IDistributeAward> distributeAwardMap) {
        this.distributeAwardMap = distributeAwardMap;
    }

    @Override
    public void saveUserAwardRecord(UserAwardRecordEntity userAwardRecordEntity) {
        String userId = userAwardRecordEntity.getUserId();
        Integer awardId = userAwardRecordEntity.getAwardId();
        String awardTitle = userAwardRecordEntity.getAwardTitle();
        // 构建消息对象
        SendAwardMessageEvent.SendAwardMessage sendAwardMessage = new SendAwardMessageEvent.SendAwardMessage();
        sendAwardMessage.setUserId(userId);
        sendAwardMessage.setAwardId(awardId);
        sendAwardMessage.setAwardTitle(awardTitle);
        sendAwardMessage.setOrderId(userAwardRecordEntity.getOrderId());
        sendAwardMessage.setAwardConfig(userAwardRecordEntity.getAwardConfig());

        BaseEvent.EventMessage<SendAwardMessageEvent.SendAwardMessage> sendAwardMessageEventMessage = sendAwardMessageEvent.buildEventMessage(sendAwardMessage);

        // 构建任务对象
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setTopic(sendAwardMessageEvent.topic());
        taskEntity.setUserId(userId);
        taskEntity.setMessageId(sendAwardMessageEventMessage.getId());
        taskEntity.setMessage(sendAwardMessageEventMessage);
        taskEntity.setState(TaskStateVO.create);

        // 构建聚合对象
        UserAwardRecordAggregate userAwardRecordAggregate = UserAwardRecordAggregate.builder()
                .userAwardRecordEntity(userAwardRecordEntity)
                .taskEntity(taskEntity)
                .build();

        // 存储用户中奖流水+中奖任务聚合对象
        awardRepository.saveUserAwardRecord(userAwardRecordAggregate);
    }

    @Override
    public void distributeAward(DistributeAwardEntity distributeAwardEntity) {
        // 奖品Key
        String awardKey = awardRepository.queryAwardKey(distributeAwardEntity.getAwardId());
        if (null == awardKey) {
            log.error("分发奖品，奖品ID不存在。awardKey:{}", awardKey);
            return;
        }

        // 奖品服务
        IDistributeAward distributeAward = distributeAwardMap.get(awardKey);

        if (null == distributeAward) {
            log.error("分发奖品，对应的服务不存在。awardKey:{}", awardKey);
            // todo 后续完善所有奖品后开启
//            throw new RuntimeException("分发奖品，奖品" + awardKey + "对应的服务不存在");
            return;
        }

        // 发放奖品
        distributeAward.giveOutPrizes(distributeAwardEntity);
    }
}
