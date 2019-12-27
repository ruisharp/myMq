package com.my.mq.commom.demo.context;

/**
 * 请求路由上下文类型
 *
 */
public enum ERouteContextType {
    /**
     * 常规请求
     */
    general,
    /**
     * 灰度路由请求。当请求标记为该模式时，请求仅路由到灰度节点服务
     */
    gray
}
