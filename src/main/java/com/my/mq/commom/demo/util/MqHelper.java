package com.my.mq.commom.demo.util;



/**
 * MQ帮助类
 *
 * @author xuchaoguo
 */
public final class MqHelper {
    private MqHelper() {

    }

    /**
     * 根据服务名生成MQ交换机名。
     *
     * @param service 服务名
     * @return 完整的MQ交换机名称
     */
    public static String getServiceExchange(String service) {
        return String.format("delicloud.%s", service);
    }

    /**
     * 生成事件在服务上的队列名
     *
     * @param producer   事件生产者服务名
     * @param event      事件名
     * @param subscriber 事件消费者的服务名
     * @param grayMode   是否灰度节点
     * @return 完整的事件MQ队列名
     */
    public static String getEventQueue(String producer, String event, String subscriber, boolean grayMode) {
        StringBuilder sb = new StringBuilder();
        sb.append(producer);
        sb.append(".").append(event);

        if (!producer.equals(subscriber)) {
            // 如果生产和消费者为同一个服务，则简化队列名称
            sb.append(".").append(subscriber);
        }
/*
        if (grayMode) {
            // 如果灰度模式，则加入灰度标记
            sb.append(".").append(ERouteContextType.gray.name());
        }*/

        return sb.toString();
    }

    /**
     * 生成队列的路由键，路由键和事件相关。
     *
     * @param event    事件名
     * @param grayMode 是否灰度节点或者灰度请求上下文
     * @return 路由键
     */
    public static String getEventRouteKey(String event, boolean grayMode) {
        StringBuilder sb = new StringBuilder();
        sb.append(event);

/*        if (grayMode) {
            // 如果灰度模式，则加入灰度标记
            sb.append(".").append(ERouteContextType.gray.name());
        }*/

        return sb.toString();
    }
}
