package com.uta.domain.award.service;

import com.uta.domain.award.event.SendAwardMessageEvent;
import com.uta.domain.award.model.aggregate.UserAwardRecordAggregate;
import com.uta.domain.award.model.entity.TaskEntity;
import com.uta.domain.award.model.entity.UserAwardRecordEntity;
import com.uta.domain.award.model.vo.AwardStateVO;
import com.uta.domain.award.model.vo.TaskStateVO;
import com.uta.domain.award.repository.IAwardRepository;
import com.uta.types.event.BaseEvent;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class AwardService implements IAwardService {

    @Resource
    private IAwardRepository awardRepository;

    @Resource
    private SendAwardMessageEvent sendAwardMessageEvent;

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

        BaseEvent.EventMessage<SendAwardMessageEvent.SendAwardMessage> sendAwardMessageEventMessage = sendAwardMessageEvent.buildEventMessage(sendAwardMessage);

        // 构建任务对象
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setTopic(sendAwardMessageEvent.topic());
        taskEntity.setUserId(userId);
        taskEntity.setMessageId(sendAwardMessageEventMessage.getId());
        taskEntity.setMessage(sendAwardMessageEventMessage);
        taskEntity.setState(TaskStateVO.created);

        // 构建聚合对象
        UserAwardRecordAggregate userAwardRecordAggregate = UserAwardRecordAggregate.builder()
                .userAwardRecordEntity(userAwardRecordEntity)
                .taskEntity(taskEntity)
                .build();

        // 存储用户中奖流水+中奖任务聚合对象
        awardRepository.saveUserAwardRecord(userAwardRecordAggregate);
    }
}
