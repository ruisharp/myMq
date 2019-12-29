package com.my.mq.commom.demo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * mq测试事件定义
 *
 */
@Getter
@AllArgsConstructor
public enum ECommonEvent {
    evt1("evt1"),
    evt2("evt2"),
    ;

    private String event;
}
