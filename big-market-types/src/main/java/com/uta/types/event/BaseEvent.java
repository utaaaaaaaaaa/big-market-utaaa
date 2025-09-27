package com.uta.types.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * MQ事件基类
 * @param <T>
 */
@Data
public abstract class BaseEvent<T> {

    /**
     * 由子类决定怎么把原始数据结构 T 包装成统一的数据格式
     * @param data 原始数据
     * @return 包装后的统一格式数据
     */
    public abstract EventMessage<T> buildEventMessage(T data);

    /**
     * 子类必须返回要发往的topic
     * @return topic
     */
    public abstract String topic();

    /**
     * 统一的MQ消息报装格式
     * @param <T>
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EventMessage<T> {
        private String id;
        private Date timestamp;
        private T data;
    }
}
