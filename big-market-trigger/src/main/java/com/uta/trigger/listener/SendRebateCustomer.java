package com.uta.trigger.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SendRebateCustomer {

    @Value("${spring.rabbitmq.topic.send_rebate}")
    private String topic;

    @RabbitListener(queuesToDeclare = @Queue("send_rebate"))
    public void listener(String message){
        try {
            log.info("监听用户行为返利发送消息 topic:{}, message:{}", topic, message);
        }catch (Exception e){
            log.error("监听用户行为返利发送消息, 消费失败 topic:{}, message:{}", topic, message);
        }
    }

}
