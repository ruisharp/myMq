package com.my.mq.commom.demo.send;


import com.my.mq.commom.demo.dto.AbstractMqMessage;
import com.my.mq.commom.demo.dto.AbstractMqRequestMessage;
import com.my.mq.commom.demo.log.LogHelper;
import com.my.mq.commom.demo.util.JacksonUtil;
import com.my.mq.commom.demo.util.MqHelper;
import com.my.mq.commom.demo.util.RandomUtil;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageBuilderSupport;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;

/**
 * 基于RabbitMQ的默认MQ事件消息发送实现
 *
 */
@Slf4j
public class DefaultMqMessageSender implements MqMessageSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final Charset UTF8 = Charset.forName("utf-8");

    @Value("${spring.application.name}")
    private String appName;

    /**
     * 发送mq消息到指定的微服务交换机
     *
     * @param message mq消息
     */
    @Override
    public void execute(AbstractMqMessage message, Map<String, Object> headers) {
        String messageId = RandomUtil.generateString(12);

        if (log.isDebugEnabled()) {
            log.debug("[{}]发送MQ消息 >> {}, {}", messageId,
                    LogHelper.toString(headers),
                    LogHelper.toString(message));
        }

        try {
            String json = JacksonUtil.bean2Json(message);

            MessageBuilderSupport<Message> builder = MessageBuilder.withBody(json.getBytes(UTF8))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setContentEncoding(UTF8.name())
                    .setMessageId(messageId)
                    .setAppIdIfAbsent(appName)
                    .setTimestampIfAbsent(new Date());

            if (headers != null && !headers.isEmpty()) {
                // 添加自定义mq消息头
                builder.copyHeaders(headers);
            }

/*            RouteRequestContext routeContext = RouteContextHolder.getRouteContext();
            // 通过header传递上下文请求类型
            Map<String, String> dataMap = routeContext.getDataMap();
            if (!dataMap.isEmpty()) {
                for (Map.Entry<String, String> pair : dataMap.entrySet()) {
                    builder.setHeader(pair.getKey(), pair.getValue());
                }
            }*/

            String targetService = this.appName;
            if (message instanceof AbstractMqRequestMessage) {
                targetService = ((AbstractMqRequestMessage) message).getService();
            }

            // 发送MQ消息
            //boolean grayMode = routeContext.getContextType().equals(ERouteContextType.gray);
            String exchange = MqHelper.getServiceExchange(targetService);
            String routeKey = MqHelper.getEventRouteKey(message.getEvent(), false);
            Message mqMessage = builder.build();

            rabbitTemplate.send(exchange, routeKey, mqMessage);
        } catch (Exception e) {
            log.error("[{}]MQ消息发送失败: {}, 错误: {}", messageId, message, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Async
    public void executeAsync(AbstractMqMessage message, Map<String, Object> headers) {
        this.execute(message, headers);
    }
}
