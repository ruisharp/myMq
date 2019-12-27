package com.my.mq.commom.demo.context;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 路由上下文
 * <p>
 * 一般情况下，灰度模式是通过http request上下文确定。
 * 对于某些非http模式下，可直接通过设置grayMode，将上下文强制设置为灰度模式。
 * <p>
 * 当grayMode为false时，默认也会判断http上下文是否会灰度模式进行综合判断。
 *
 */
public class RouteRequestContext {
    private static final String HEAD_ROUTE_KEY_PREFIX = "_route_";
    /**
     * 请求上下文类型
     */
    private static final String ROUTE_CONTEXT_TYPE = HEAD_ROUTE_KEY_PREFIX + "context_type_";
    /**
     * 用户上下文
     */
    private static final String ROUTE_USER_CONTEXT = HEAD_ROUTE_KEY_PREFIX + "user_context_";

    /**
     * HTTP请求上下文
     */
    private RequestAttributes requestContext;

    /**
     * 上下文信息存储
     */
    @Getter
    private Map<String, String> dataMap;

    public RouteRequestContext() {
        this.dataMap = new HashMap<>();
        this.requestContext = RequestContextHolder.getRequestAttributes();
    }

    public RouteRequestContext(ERouteContextType contextType) {
        this();
        this.setContextType(contextType);
    }

    /**
     * 获取当前上下文请求的类型
     *
     * @return 请求类型
     */
    public ERouteContextType getContextType() {
        String result = getData(ROUTE_CONTEXT_TYPE);
        if (!StringUtils.isEmpty(result)) {
            return ERouteContextType.valueOf(result);
        } else {
            // 默认是常规路由上下文
            return ERouteContextType.general;
        }
    }

/*    *//**
     * 获取当前用户上下文。如果没有，则返回NULL
     *
     * @return 用户上下文
     *//*
    public UserContext getUserContext() {
        String result = getData(ROUTE_USER_CONTEXT);
        if (!StringUtils.isEmpty(result)) {
            return UserContext.fromString(result);
        } else {
            // 缺省无用户上下文
            return null;
        }
    }*/

/*    *//**
     * 设置用户上下文（app网关专用）
     *
     * @param request     app网关请求
     * @param userContext 用户上下文
     * @return 重新构建的app网关请求对象
     *//*
    public static ServerHttpRequest setUserContext(ServerHttpRequest request,
                                                   UserContext userContext) {
        return request.mutate().header(ROUTE_USER_CONTEXT,
                UserContext.toString(userContext)).build();
    }*/

    /**
     * 设置路由上下文类型
     *
     * @param contextType 上下文类型
     */
    public void setContextType(ERouteContextType contextType) {
        this.dataMap.put(ROUTE_CONTEXT_TYPE, contextType.name());
    }

/*    *//**
     * 设置用户上下文
     *
     * @param userContext 用户上下文信息
     *//*
    public void setUserContext(UserContext userContext) {
        if (userContext != null) {
            this.dataMap.put(ROUTE_USER_CONTEXT, UserContext.toString(userContext));
        } else {
            this.dataMap.remove(ROUTE_USER_CONTEXT);
        }
    }*/

    /**
     * 直接保存路由上下文信息。 如果并非符合规则的键，自动忽略。
     * 如果值是NULL，则视为删除
     *
     * @param routeKey   键
     * @param routeValue 值
     */
    public void setData(String routeKey, Object routeValue) {
        if (!routeKey.startsWith(HEAD_ROUTE_KEY_PREFIX)) {
            // 非路由上下文配置
            return;
        }

        if (routeValue == null) {
            this.dataMap.remove(routeKey);
        } else {
            this.dataMap.put(routeKey, String.valueOf(routeValue));
        }
    }

    /**
     * 通过键查询路由上下文对应信息。逻辑上先从本地缓存查，如果没有则从http请求上下文查。
     * 查找成功会自动进行本地缓存。
     *
     * @param routeKey 键
     * @return 值。查找失败则返回NULL
     */
    public String getData(String routeKey) {
        String value = this.dataMap.get(routeKey);
        if (value == null) {
            // 本地缓存不存在，则从http请求上下文获取
            if (requestContext != null
                    && requestContext instanceof ServletRequestAttributes) {
                ServletRequestAttributes sra = (ServletRequestAttributes) requestContext;
                HttpServletRequest request = sra.getRequest();
                value = request.getHeader(routeKey);
                if (!StringUtils.isEmpty(value)) {
                    // 如果HTTP请求头存在，则存储到本地并返回
                    this.dataMap.put(routeKey, value);
                }
            }
        }

        return value;
    }
}
