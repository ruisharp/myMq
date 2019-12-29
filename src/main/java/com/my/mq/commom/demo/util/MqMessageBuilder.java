package com.my.mq.commom.demo.util;


import com.my.mq.commom.demo.dto.AbstractMqEventMessage;
import com.my.mq.commom.demo.dto.AbstractMqMessage;
import com.my.mq.commom.demo.dto.AbstractMqRequestMessage;
import com.my.mq.commom.demo.dto.JsonBase;

/**
 * @param <T> mq消息类型
 * @author xuchaoguo
 */
public class MqMessageBuilder<T extends AbstractMqMessage> {
    private T instance;

    /**
     * 构造MQ消息
     */
    public MqMessageBuilder(Class<T> clazz) {
        try {
            instance = clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("实例化失败");
        }
    }

    /**
     * 设置事件核心数据，包括事件名称和事件数据
     *
     * @param event     事件名称
     * @param eventData 事件数据
     * @return 构造器
     */
    @SuppressWarnings("unchecked")
    public MqMessageBuilder withEvent(String event, JsonBase eventData) {
        instance.setEvent(event);
        instance.setEventData(eventData);
        return this;
    }

    /**
     * 设置mq消息时间，默认为当前时间
     *
     * @param timestamp 时间戳（毫秒）
     * @return 构造器
     */
    public MqMessageBuilder withTimestamp(long timestamp) {
        instance.setTimestamp(timestamp);
        return this;
    }

    /**
     * 设置mq消息版本，用于多版本迭代兼容
     *
     * @param version 版本号
     * @return 构造器
     */
    public MqMessageBuilder withVersion(int version) {
        instance.setVersion(version);
        return this;
    }

    /**
     * 构造mq异步请求消息
     *
     * @param service 目标微服务名
     * @return 消息实例
     */
    public T buildRequestMessage(String service) {
        if (instance instanceof AbstractMqRequestMessage) {
            ((AbstractMqRequestMessage) instance).setService(service);

            if (instance.isValid()) {
                return instance;
            }
        }

        throw new RuntimeException("实例化失败");
    }

    /**
     * 构造mq广播事件消息
     *
     * @param before 事件发生前的业务状态
     * @param after  事件发生后的业务状态
     * @return 消息实例
     */
    @SuppressWarnings("unchecked")
    public T buildEventMessage(JsonBase before, JsonBase after) {
        if (instance instanceof AbstractMqEventMessage) {
            ((AbstractMqEventMessage) instance).setBefore(before);
            ((AbstractMqEventMessage) instance).setAfter(after);

            if (instance.isValid()) {
                return instance;
            }

            return instance;
        }

        throw new RuntimeException("实例化失败");
    }
}
