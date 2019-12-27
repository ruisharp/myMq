package com.my.mq.commom.demo.send;

import com.my.mq.commom.demo.dto.AbstractMqMessage;
import java.util.Map;

/**
 * MQ事件消息发送接口。

 */
public interface MqMessageSender {

    /**
     * 发送MQ消息。
     *
     * @param message MQ消息，包括异步请求消息MqRequestMessage以及广播事件消息MqEventMessage
     * @param headers 自定义mq消息头
     */
    void execute(AbstractMqMessage message, Map<String, Object> headers);

    /**
     * 异步发送MQ消息
     *
     * @param message MQ消息，包括异步请求消息MqRequestMessage以及广播事件消息MqEventMessage
     * @param headers 自定义mq消息头
     */
    void executeAsync(AbstractMqMessage message, Map<String, Object> headers);

    /**
     * 发送MQ消息，无自定义mq消息头简化版本。
     *
     * @param message MQ消息
     */
    default void execute(AbstractMqMessage message) {
        this.execute(message, null);
    }
}
