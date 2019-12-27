package com.my.mq.commom.demo.dto;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * MQ事件队列配置信息
 *
 * @author xuchaoguo
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MqEventProperties extends JsonBase {
    /**
     * MQ消费者一次拉取消息数量
     */
    private int prefetch;
    /**
     * MQ消费者最小数量
     */
    private int minConcurrency;
    /**
     * MQ消费者最大数量
     */
    private int maxConcurrency;
    /**
     * 是否消息处理自动确认
     */
    private boolean autoAck;
    /**
     * 是否重入队列
     */
    private boolean requeue;
    /**
     * 是否持久化
     */
    private boolean durable;
    /**
     * 是否动态配置。
     * 如果启用，则不会自动加载该配置
     */
    private boolean dynamic;
    /**
     * 事件产生的服务名称，对应了唯一的MQ交换机
     * 如果未设置，则视为当前服务
     */
    private String service;
    /**
     * 事件列表，一个事件对应一个MQ队列
     */
    private List<String> events;

    public MqEventProperties() {
        this.prefetch = 1;
        this.minConcurrency = 1;
        this.maxConcurrency = 1;
        this.autoAck = true;
        this.requeue = false;
        this.durable = true;
        this.dynamic = false;
    }

    /**
     * 是否有效配置
     *
     * @return 如果是返回true, 否则false
     */
    public boolean isValid() {
        return events != null && !events.isEmpty();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
