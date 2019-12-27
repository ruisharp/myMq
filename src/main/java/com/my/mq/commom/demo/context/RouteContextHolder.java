package com.my.mq.commom.demo.context;

import org.springframework.core.NamedThreadLocal;

/**
 * 路由上下文存储器
 *
 */
public final class RouteContextHolder {
    /**
     * 通过本地线程池保存路由上下文
     */
    private static final ThreadLocal<RouteRequestContext> ROUTE_CONTEXTS = new NamedThreadLocal("Route Contexts");

    private RouteContextHolder() {

    }

    /**
     * 获取路由上下文
     *
     * @return 上下文
     */
    public static RouteRequestContext getRouteContext() {
        RouteRequestContext context = ROUTE_CONTEXTS.get();
        if (context == null) {
            context = new RouteRequestContext();
            RouteContextHolder.setRouteContext(context);
        }

        return context;
    }

    /**
     * 设置路由上下文
     *
     * @param context 上下文
     */
    public static void setRouteContext(RouteRequestContext context) {
        ROUTE_CONTEXTS.set(context);
    }

    /**
     * 重置上下文
     * 参考：RequestContextHolder.resetRequestAttributes();
     */
    public static void resetRouteContext() {
        ROUTE_CONTEXTS.remove();
    }
}

