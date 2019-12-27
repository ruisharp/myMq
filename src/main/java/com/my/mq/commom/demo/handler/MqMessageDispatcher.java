package com.my.mq.commom.demo.handler;

import com.my.mq.commom.demo.config.MqConfig;
import com.my.mq.commom.demo.context.RouteContextHolder;
import com.my.mq.commom.demo.context.RouteRequestContext;
import com.my.mq.commom.demo.dto.AbstractMqMessage;
import com.my.mq.commom.demo.util.JacksonUtil;
import com.rabbitmq.client.Channel;
import java.nio.charset.Charset;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ConditionalOnBean(MqConfig.class)
public class MqMessageDispatcher implements ChannelAwareMessageListener {

  @Autowired
  private MqMessageHandlerRegister handlerRegister;


  private static final Charset UTF8 = Charset.forName("utf-8");

  @Override
  public void onMessage(Message message, Channel channel) throws Exception {
    MessageProperties messageProperties = message.getMessageProperties();
    Map<String, Object> headers = messageProperties.getHeaders();

    String messageId = messageProperties.getMessageId();
    String json = new String(message.getBody(), UTF8);
    String eventExchange = messageProperties.getReceivedExchange();

    if (log.isDebugEnabled()) {
      log.debug("[{}]接收到来自{}的MQ消息", messageId, eventExchange);
    }

    long ts = System.currentTimeMillis();
    try {
      AbstractMqMessage mqMessage = JacksonUtil.json2Bean(json, AbstractMqMessage.class);

      // 从事件组的处理器中查找对应事件的处理器
      String event = mqMessage.getEvent();

      MqMessageHandler handler = handlerRegister.getHandler(eventExchange, event);
      if (handler != null) {
        try {
          // 恢复上下文
          RouteRequestContext routeContext = RouteContextHolder.getRouteContext();
          for (Map.Entry<String, Object> pair : headers.entrySet()) {
            routeContext.setData(pair.getKey(), pair.getValue());
          }

  /*        if (!handler.restoreUserContext(mqMessage)) {
            // 如果请求不需要恢复用户上下文，则删除掉。
            routeContext.setUserContext(null);
          }*/

          handler.onMessage(mqMessage, messageProperties);
        } finally {
          // 一旦处理完毕，立刻清理线程上下文信息，避免复用造成问题
          RouteContextHolder.resetRouteContext();
        }
      } else {
        // 视为不处理，直接丢弃
        if (log.isDebugEnabled()) {
          log.debug("[{}]MQ消息找不到事件处理器: {}.{}", messageId, eventExchange, event);
        }
      }
    } catch (Exception e) {
      // 消息处理失败，发送到DLX队列保存
      log.error("[{}]MQ消息处理失败: {}, 错误: {}", messageId, json, e.getMessage(), e);
      //ExceptionHandlerManager.onException(e);
      throw e;
    } finally {
      if (log.isDebugEnabled()) {
        log.debug("[{}]MQ消息处理时长: {}", messageId, (System.currentTimeMillis() - ts));
      }
    }
  }
}

