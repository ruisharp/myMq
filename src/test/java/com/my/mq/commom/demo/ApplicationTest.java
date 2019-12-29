package com.my.mq.commom.demo;


import com.my.mq.commom.demo.bo.MqTestData;
import com.my.mq.commom.demo.bo.TestData;
import com.my.mq.commom.demo.context.RouteContextHolder;
import com.my.mq.commom.demo.enums.ECommonEvent;
import com.my.mq.commom.demo.handler.CommonEventHandler;
import com.my.mq.commom.demo.send.MqMessageSender;
import com.my.mq.commom.demo.util.MqMessageBuilder;
import java.util.Date;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Assert;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ApplicationTest {
    private MockMvc mockMvc;

    @Autowired
    CommonEventHandler commonEventHandler;

    @Autowired
    MqMessageSender mqMessageSender;

/*    @Autowired
    private KafkaProducer kafkaProducer;*/

    @SpringBootApplication(scanBasePackages = "com.my")
    static class Config {
    }

//    @Before
//    public void setUp() throws Exception {
//        mockMvc = MockMvcBuilders.standaloneSetup(
//                new ApiController()).build();
//    }
//
//    @Test
//    public void test1() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0/hello/").accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andDo(MockMvcResultHandlers.print())
//                .andReturn();
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/v2.0/hello/").accept(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andDo(MockMvcResultHandlers.print())
//                .andReturn();
//    }

    @Test
    public void mqTest() throws Exception {
        ECommonEvent[] testEvents = ECommonEvent.values();
/*        UserContext userContext = new UserContext("token123", 1L, null, "test");
        RouteContextHolder.getRouteContext().setUserContext(userContext);*/

        int messageCount = 20;
        for (int i = 0; i < messageCount; i++) {
            TestData data = new TestData();
            data.setAge(i);
            data.setDate(new Date());
            data.setName("mqHello_" + i);
            data.setCreateTime(System.currentTimeMillis());

            // 随机发送
            int index = i % testEvents.length;
            ECommonEvent event = testEvents[index];

            MqMessageBuilder builder = new MqMessageBuilder<>(MqTestData.class);
            builder.withEvent(event.getEvent(), data);
            builder.withVersion(1);
            MqTestData mqMessage = (MqTestData) builder.buildRequestMessage(Consts.SERVICE_NAME);
            mqMessageSender.execute(mqMessage);
        }
        //等待消费完
        Thread.sleep(3000);

        Assert.isTrue(commonEventHandler.getCount().get() == messageCount, "mq消息处理失败");
    }

/*    @Test
    public void kafkaTest() {
        String topic = "common-service";
        if (!kafkaProducer.existTopic(topic)) {
            kafkaProducer.createTopic(topic);
        }

        Assert.isTrue(kafkaProducer.existTopic(topic), "kafka主题创建失败");

        int messageCount = 100;
        for (int i = 0; i < messageCount; i++) {
            TestData data = new TestData();
            data.setAge(i);
            data.setDate(new Date());
            data.setName("kafkaHello_" + i);
            data.setCreateTime(System.currentTimeMillis());

            kafkaProducer.sendMessage(topic, data);
        }
    }*/
}
