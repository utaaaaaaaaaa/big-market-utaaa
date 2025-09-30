package com.uta.infrastructure.event;

import com.alibaba.fastjson.JSON;
import com.uta.types.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publish(String topic, BaseEvent.EventMessage<?> eventMessage){
        try {
            String message = JSON.toJSONString(eventMessage);
            rabbitTemplate.convertAndSend(topic, message);
            log.info("发送MQ消息, topic:{}, message:{}", topic, message);
        }catch (Exception e){
            log.error("发送MQ消息失败, topic:{}, message:{}",topic, JSON.toJSONString(eventMessage), e);
            throw e;
        }
    }

    public void publish(String topic, String eventMessageJson){
        try {
            rabbitTemplate.convertAndSend(topic, eventMessageJson);
            log.info("发送MQ消息, topic:{}, message:{}", topic, eventMessageJson);
        }catch (Exception e){
            log.error("发送MQ消息失败, topic:{}, message:{}",topic, eventMessageJson, e);
            throw e;
        }
    }


}
