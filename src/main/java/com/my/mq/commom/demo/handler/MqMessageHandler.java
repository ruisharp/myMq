package com.my.mq.commom.demo.handler;

import com.my.mq.commom.demo.dto.AbstractMqMessage;
import com.my.mq.commom.demo.dto.AbstractMqRequestMessage;
import org.springframework.amqp.core.MessageProperties;

/**
 * MQ消息处理器。
 * <p>
 * 注意，该消息处理器仅支持监听处理某一个指定服务的事件列表，多个不同服务的事件请分开定义。
 * <p>
 * 另外，用于也可以每个事件单独定义一个处理器，特别是事件业务比较独立时，我们推荐这样做。
 *
 * @param <T> mq消息类型
 */
public interface MqMessageHandler<T extends AbstractMqMessage> {
    /**
     * 所有事件标记，关注所有事件的处理器视为默认缺省处理器
     */
    String[] ALL_EVENTS = new String[0];

    /**
     * 监听事件的源生产服务名称。
     *
     * @return 服务名称
     */
    String service();

    /**
     * 监听的事件列表
     *
     * @return 事件名称列表
     */
    String[] events();

    /**
     * 是否恢复消息用户上下文。默认情况下，如果是mq异步请求，则保留上下文
     * 如果是广播事件，则不保留。
     *
     * @param message 消息
     * @return 如果返回true，则在mq消息接收后，恢复当前线程上下文为发送方的用户上下文。否则忽略上下文
     */
    default boolean restoreUserContext(T message) {
        return message instanceof AbstractMqRequestMessage;
    }

    /**
     * 处理MQ队列消息
     *
     * @param message      MQ消息
     * @param mqProperties MQ消息属性信息
     * @throws Exception 当消息处理失败抛出异常时，消息会默认转入DLX死信队列
     */
    void onMessage(T message, MessageProperties mqProperties) throws Exception;
}
