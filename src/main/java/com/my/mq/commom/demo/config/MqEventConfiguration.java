package com.my.mq.commom.demo.config;

import com.my.mq.commom.demo.dto.MqEventProperties;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 读取自定义队列配置

 */
@Data
@Configuration
@ConfigurationProperties(prefix = "my.mq")
public class MqEventConfiguration {
    /**
     * 队列配置列表
     */
    private List<MqEventProperties> eventGroups = new ArrayList<>();
}
