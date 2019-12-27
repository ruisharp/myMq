package com.my.mq.commom.demo.manager;

import com.my.mq.commom.demo.dto.MqEventProperties;
import lombok.Getter;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;

/**
 * MQ事件队列定义信息，包括交换机、队列以及消费者监听器配置等等
 *
 */
@Getter
public final class MqQueueDef {
    /**
     * 队列配置属性
     */
    private MqEventProperties queueProperty;

    /**
     * 队列消息监听器容器实例，包含了消费者以及消费策略信息
     */
    private SimpleMessageListenerContainer queueListenerContainer;

    public MqQueueDef(MqEventProperties eventProperties, ConnectionFactory connectionFactory, ChannelAwareMessageListener messageListener) {
        this.queueProperty = eventProperties;

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);

        container.setPrefetchCount(Math.max(1, eventProperties.getPrefetch()));
        //设置默认当前消费者数量
        container.setConcurrentConsumers(Math.max(1, eventProperties.getMinConcurrency()));
        //并发消费设置最大消费者数量,并发消费的时候需要设置,且>=concurrentConsumers
        container.setMaxConcurrentConsumers(Math.max(eventProperties.getMinConcurrency(),
                eventProperties.getMaxConcurrency()));
        //投递失败时是否重新排队
        container.setDefaultRequeueRejected(eventProperties.isRequeue());
        //消息确认机制
        container.setAcknowledgeMode(eventProperties.isAutoAck() ? AcknowledgeMode.AUTO : AcknowledgeMode.MANUAL);
        //需要将channel暴露给listener才能手动确认,AcknowledgeMode.MANUAL时必须为true
        container.setExposeListenerChannel(true);
        String beanName = String.format("mqContainer-%d", eventProperties.hashCode());
        container.setBeanName(beanName);
        //设置listener
        container.setMessageListener(messageListener);

        this.queueListenerContainer = container;
    }

    /**
     * 配置是否有效
     *
     * @return 如果有效，则为true，否则false
     */
    public boolean isValid() {
        return queueProperty != null && queueProperty.isValid() && queueListenerContainer != null;
    }

    @Override
    public String toString() {
        return queueProperty.toString();
    }
}
