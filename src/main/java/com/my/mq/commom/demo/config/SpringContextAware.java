package com.my.mq.commom.demo.config;

import com.my.mq.commom.demo.util.SpringBeanUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

/**
 * Spring上下文
 *
 * @author xuchaoguo
 */
@Configuration
public class SpringContextAware implements ApplicationContextAware {
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringBeanUtil.setAppContext(applicationContext);
    }
}
