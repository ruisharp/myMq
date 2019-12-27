package com.my.mq.commom.demo.listern;


import com.my.mq.commom.demo.config.MqConfig;
import com.my.mq.commom.demo.config.MqEventConfiguration;
import com.my.mq.commom.demo.dto.MqEventProperties;
import com.my.mq.commom.demo.handler.MqMessageDispatcher;
import com.my.mq.commom.demo.manager.MqQueueDef;
import com.my.mq.commom.demo.manager.MqQueueManager;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 加载MQ自定义配置信息.
 * <p>
 * 在应用启动完成后，会读取配置文件，并自动初始化MQ队列以及消费者。
 * <p>
 * 具体配置项请参考MqEventProperties对象。
 *
 */
@Component
@Slf4j
@ConditionalOnBean(MqConfig.class)
public class MqEventConfigurationInitializer implements ApplicationListener<ApplicationReadyEvent> {
    @Autowired
    private MqEventConfiguration mqEventConfiguration;

    @Autowired
    private MqQueueManager mqQueueManager;

    @Autowired
    private MqMessageDispatcher messageDispatcher;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private SimpleRabbitListenerContainerFactory listenerContainerFactory;

    private boolean initFlag = false;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        if (initFlag) {
            log.warn("MQ配置重复加载自动忽略!");
            return;
        }

        initFlag = true;
        List<MqEventProperties> eventGroups = mqEventConfiguration.getEventGroups();
        if (eventGroups == null || eventGroups.isEmpty()) {
            log.warn("未配置MQ事件队列信息!");
            return;
        }

        for (MqEventProperties eventProperties : eventGroups) {
            MqQueueDef mqQueueDef = new MqQueueDef(eventProperties, connectionFactory, messageDispatcher);
            //如果系统有拦截器需要复制拦截器到监听器中
            mqQueueDef.getQueueListenerContainer().setAdviceChain(listenerContainerFactory.getAdviceChain());
            if (!mqQueueDef.getQueueProperty().isDynamic()) {
                // 如果配置无效，可能是通过程序动态加载，则不需要初始化
                mqQueueManager.addQueueDef(mqQueueDef);
            }
        }

        log.info("MQ配置初始化完成.");
    }
}
