package com.my.mq.commom.demo.manager;


import com.my.mq.commom.demo.config.MqConfig;
import com.my.mq.commom.demo.dto.MqEventProperties;
import com.my.mq.commom.demo.util.MqHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 基于Rabbitmq的消息队列管理器.
 * <p>
 * 消息队列需要监听灰度配置，用于自动更新队列名称将灰度消息分流到灰度服务。

 */
@Component
@Slf4j
@ConditionalOnBean(value = {MqConfig.class})
public class MqQueueManager{
    private static final String DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";
    private static final String DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";

    @Value("${spring.application.name}")
    private String service;

    @Value("${eureka.instance.metadata-map.version}")
    private String serviceVersion;

/*    @Autowired
    private RouteConfigManager routeConfigMgr;*/

    @Autowired
    private RabbitAdmin rabbitAdmin;

    private List<MqQueueDef> mqQueueDefs = new ArrayList<>();

    private boolean grayNode;
/*

    @PostConstruct
    private void onStart() {
        this.grayNode = false;
        routeConfigMgr.addListener(this);
    }

    @PreDestroy
    private void onStop() {
        routeConfigMgr.removeListener(this);

        if (mqQueueDefs.size() > 0) {
            for (MqQueueDef queueDef : mqQueueDefs) {
                SimpleMessageListenerContainer listenerContainer = queueDef.getQueueListenerContainer();
                if (listenerContainer.isRunning()) {
                    listenerContainer.stop();
                }
            }
        }
    }
*/

    /**
     * 添加新的队列定义
     *
     * @param queueDef 队列定义
     */
    public void addQueueDef(MqQueueDef queueDef) {
        mqQueueDefs.add(queueDef);
        buildQueues(queueDef);
    }

/*    @Override
    public void onUpdate(Map<String, ServiceGrayConfig> newConfig) {
        ServiceGrayConfig serviceConfig = routeConfigMgr.getServiceConfig(service);
        boolean grayNode = serviceConfig != null
                && serviceVersion.equalsIgnoreCase(serviceConfig.getVersion());

        if (grayNode == this.grayNode) {
            log.info("服务节点路由配置无变更，直接忽略！");
            return;
        }

        this.grayNode = grayNode;
        log.info("检测路由配置信息更新，灰度节点标记:{}", grayNode);

        // 重新刷新队列
        if (mqQueueDefs.size() > 0) {
            for (MqQueueDef queueDef : mqQueueDefs) {
                if (!this.grayNode) {
                    // 如果从灰度模式切换非灰度模式，需要等待消息消费结束
                    // 否则，强制结束会导致灰度消息未消费或消费异常进入DLX队列
                    waitForGroupQueuesClean(queueDef, 60000, 5000);
                }

                buildQueues(queueDef);
            }
        }
    }*/

    /**
     * 根据队列定义，自动创建并绑定队列。
     *
     * @param queueDef 队列定义
     */
    public void buildQueues(MqQueueDef queueDef) {
        log.info("构建MQ事件队列: {}", queueDef);

        if (!queueDef.isValid()) {
            log.error("队列配置异常，构建失败！");
            return;
        }

        // 启动消费者监听器
        SimpleMessageListenerContainer listenerContainer = queueDef.getQueueListenerContainer();
        if (!listenerContainer.isRunning()) {
            listenerContainer.start();
        }

        MqEventProperties eventProperties = queueDef.getQueueProperty();
        String eventService = eventProperties.getService();
        if (StringUtils.isEmpty(eventService)) {
            // 如果未指定事件的服务，则视为当前服务的事件监听
            eventService = service;
        }

        // 交换机默认由事件生产者创建。但如果未创建则自动创建（初次启动顺序）
        String exchangeName = MqHelper.getServiceExchange(eventService);
        Exchange eventExchange = new TopicExchange(exchangeName);
        try {
            rabbitAdmin.declareExchange(eventExchange);
        } catch (Exception e) {
            // 可能是已经存在
            log.warn("创建交换机{}失败: {}", exchangeName, e.getMessage());
        }

        Map<String, Object> queueParams = new HashMap<>(2);

        List<String> queueNames = new ArrayList<>();
        List<String> events = eventProperties.getEvents();
        for (String event : events) {
            String queueName = MqHelper.getEventQueue(eventService, event, service, grayNode);
            String routeKey = MqHelper.getEventRouteKey(event, grayNode);

            // 设置DLX队列名称
            String dlxQueueName = String.format("%s.dlq", queueName);
            String dlxQueueRouteKey = String.format("%s.dlq", routeKey);

            queueParams.put(DEAD_LETTER_ROUTING_KEY, dlxQueueRouteKey);
            queueParams.put(DEAD_LETTER_EXCHANGE, exchangeName);

            // 声明交换机（与事件组名称一致）
            boolean durable = eventProperties.isDurable();

            // 声明队列（服务名结合事件名，不同服务使用不同队列）
            Queue queue = new Queue(queueName, durable, false, !durable, queueParams);
            rabbitAdmin.declareQueue(queue);
            rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(eventExchange).with(routeKey).noargs());
            // 声明队列的DLX死信队列
            Queue deadQueue = new Queue(dlxQueueName, durable, false, !durable);
            rabbitAdmin.declareQueue(deadQueue);
            rabbitAdmin.declareBinding(
                BindingBuilder.bind(deadQueue).to(eventExchange).with(dlxQueueRouteKey).noargs());
            queueNames.add(queueName);
        }

        String[] array = new String[queueNames.size()];
        array = queueNames.toArray(array);
        queueDef.getQueueListenerContainer().setQueueNames(array);
    }

    /**
     * 检查分组队列中是否所有消息都已消费完毕
     *
     * @param queueDef 分组队列定义
     * @return 如果全部消费完毕，则返回true，否则false
     */
    private boolean isAllQueueClean(MqQueueDef queueDef) {
        String[] queueNames = queueDef.getQueueListenerContainer().getQueueNames();
        if (queueNames.length == 0) {
            return true;
        }

        for (String queueName : queueNames) {
            Object value = rabbitAdmin.getQueueProperties(queueName)
                    .get(String.valueOf(RabbitAdmin.QUEUE_MESSAGE_COUNT));
            if (value == null) {
                continue;
            }
            int count = Integer.parseInt(String.valueOf(value));
            if (count > 0) {
                log.info("检查到队列[{}]仍然有未消费的消息数: {}", queueName, count);
                return false;
            }
        }

        return true;
    }

    /**
     * 同步阻塞等待消息队列清空
     *
     * @param queueDef    队列组定义
     * @param waitTime    等待毫秒数
     * @param checkPeriod 检查毫秒数间隔
     */
    private void waitForGroupQueuesClean(MqQueueDef queueDef, long waitTime, long checkPeriod) {
        long timePassed = 0L;

        try {
            while (timePassed < waitTime) {
                if (isAllQueueClean(queueDef)) {
                    break;
                }

                timePassed += checkPeriod;
                Thread.sleep(checkPeriod);
            }
        } catch (Exception e) {
            log.warn("检查队列消息数量中断.", e);
        }
    }
}
