package com.my.mq.commom.demo.handler;

import com.my.mq.commom.demo.Consts;
import com.my.mq.commom.demo.bo.MqTestData;
import com.my.mq.commom.demo.context.RouteContextHolder;
import com.my.mq.commom.demo.enums.ECommonEvent;
import com.my.mq.commom.demo.util.JacksonUtil;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@Slf4j
public class CommonEventHandler implements MqMessageHandler<MqTestData> {
    @Getter
    private AtomicInteger count = new AtomicInteger(0);

    @Override
    public String service() {
        return Consts.SERVICE_NAME;
    }

    @Override
    public String[] events() {
        // 监听event1事件组下的所有事件
        return ALL_EVENTS;
    }

    @Override
    public void onMessage(MqTestData message, MessageProperties properties) {
        log.info("接受到消息数= {}", count.incrementAndGet());

        //UserContext userContext = RouteContextHolder.getRouteContext().getUserContext();
        //log.info("userContext= {}", JacksonUtil.bean2Json(userContext));
        //Assert.isTrue(userContext.getUserId() == 1, "上下文传递失败");

        if (message.getEvent().equals(ECommonEvent.evt1.getEvent())) {
            // 送到DLQ队列
            throw new RuntimeException("测试evt1消息，抛出异常！");
        }
    }
}
