package com.my.mq.commom.demo.handler;


import com.my.mq.commom.demo.config.MqConfig;
import com.my.mq.commom.demo.util.MqHelper;
import com.my.mq.commom.demo.util.SpringBeanUtil;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;


@Configuration
@Order(1)
@ConditionalOnBean(MqConfig.class)
public class MqMessageHandlerRegister implements ApplicationListener<ApplicationReadyEvent> {
    /**
     * 事件处理器映射表，key标示事件处理交换机名称，value标示事件以及对应的处理器映射关系
     */
    private Map<String, Map<String, MqMessageHandler>> groupMessageHandlers = new HashMap<>();
    /**
     * 默认的事件处理器映射表，key标示事件处理交换机名称，value标示该交换机下的所有事件的默认处理器
     * 默认处理器仅在未指定具体事件处理器的情况下被使用。
     */
    private Map<String, MqMessageHandler> groupDefaultMessageHandlers = new HashMap<>();

    @Override
    public void onApplicationEvent(ApplicationReadyEvent readyEvent) {
        if (!this.groupMessageHandlers.isEmpty()) {
            return;
        }

        Map<String, MqMessageHandler> beans = SpringBeanUtil.getBeansOfType(MqMessageHandler.class);
        for (MqMessageHandler bean : beans.values()) {
            String eventExchange = MqHelper.getServiceExchange(bean.service());
            String[] events = bean.events();
            if (StringUtils.isEmpty(eventExchange) || events == null) {
                throw new RuntimeException("事件配置不正确:" + bean.getClass().getCanonicalName());
            }

            if (events == MqMessageHandler.ALL_EVENTS) {
                // 如果设置为全部监听，则标记为缺省处理器。
                this.groupDefaultMessageHandlers.put(eventExchange, bean);
            } else {
                Map<String, MqMessageHandler> eventHandlers = this.groupMessageHandlers.get(eventExchange);
                if (eventHandlers == null) {
                    eventHandlers = new HashMap<>(beans.size());
                    this.groupMessageHandlers.put(eventExchange, eventHandlers);
                }

                for (String event : events) {
                    eventHandlers.put(event, bean);
                }
            }
        }
    }

    /**
     * 查找mq事件处理器。
     * 逻辑上会先根据事件名找到指定处理器，如果不存在，则会采用服务默认的事件处理器
     *
     * @param eventService 事件所属的服务（事件处理交换机）
     * @param event        事件
     * @return 处理器。如果未发现，则返回NULL
     */
    public MqMessageHandler getHandler(String eventService, String event) {
        MqMessageHandler handler = null;
        Map<String, MqMessageHandler> eventHandlers = groupMessageHandlers.get(eventService);
        if (eventHandlers != null) {
            handler = eventHandlers.get(event);
        }

        if (handler == null) {
            // 如果无事件对应处理器，则查找默认处理器替代
            handler = groupDefaultMessageHandlers.get(eventService);
        }

        return handler;
    }
}
