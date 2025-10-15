package com.uta.domain.award.event;

import com.uta.types.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 发送奖品消息
 */
@Component
public class SendAwardMessageEvent extends BaseEvent<SendAwardMessageEvent.SendAwardMessage> {

    @Value("${spring.rabbitmq.topic.send_award}")
    private String topic;

    @Override
    public EventMessage<SendAwardMessage> buildEventMessage(SendAwardMessage data) {
        return EventMessage.<SendAwardMessage>builder()
                .data(data)
                .id(RandomStringUtils.randomNumeric(11))
                .timestamp(new Date())
                .build();
    }

    @Override
    public String topic() {
        return this.topic;
    }

    /**
     * 消息体
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SendAwardMessage{
        private String userId;
        private String orderId;
        private Integer awardId;
        private String awardTitle;
        private String awardConfig;
    }

}
