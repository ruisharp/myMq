package com.my.mq.commom.demo.config;


import com.my.mq.commom.demo.send.DefaultMqMessageSender;
import com.my.mq.commom.demo.send.MqMessageSender;
import com.my.mq.commom.demo.util.MqHelper;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;


@Configuration
public class MqConfig {

    @Value("${spring.application.name}")
    private String service;

    /**
     * 加载默认的mq消息发送器
     *
     * @return MqMessageSender 默认bean
     */
    @Bean
    @ConditionalOnMissingBean(MqMessageSender.class)
    public MqMessageSender mqMessageSender() {
        return new DefaultMqMessageSender();
    }

    /**
     * 创建RabbitMQ管理员，用于动态创建MQ相关的交换器、队列以及绑定关系
     *
     * @param connectionFactory mq连接工厂
     * @return rabbitmq 管理员
     */
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    /**
     * 创建当前服务的事件交换机。
     * 如果服务有自定义创建的需求，则不创建，可单独定义。
     *
     * @return 当前服务的事件交换机
     */
    @Bean("serviceExchange")
    @Order(1)
    @ConditionalOnMissingBean(name = "serviceExchange")
    public Exchange serviceExchange() {
        return new TopicExchange(MqHelper.getServiceExchange(service), true, false);
    }
}
