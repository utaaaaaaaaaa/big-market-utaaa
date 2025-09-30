package com.uta.domain.award.model.entity;

import com.uta.domain.award.event.SendAwardMessageEvent;
import com.uta.domain.award.model.vo.TaskStateVO;
import com.uta.types.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskEntity {

    /**
     * 消息主题
     */
    private String topic;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 消息id
     */
    private String messageId;

    /**
     * 消息主体
     */
    private BaseEvent.EventMessage<SendAwardMessageEvent.SendAwardMessage> message;

    /**
     * 任务状态；create-创建、completed-完成、fail-失败
     */
    private TaskStateVO state;

}
