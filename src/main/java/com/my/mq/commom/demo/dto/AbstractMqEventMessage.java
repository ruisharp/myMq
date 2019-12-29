package com.my.mq.commom.demo.dto;

import com.my.mq.commom.demo.util.JacksonUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 广播事件类MQ消息继承本类。
 * <p>
 * 对于广播事件，业务系统除了关注事件本身的数据之外，还关注事件发生之间到事件发生之后的数据变化。
 *
 * @param <S> 事件上下文状态变更对象
 * @param <T> 事件关键数据对象
 */
@ApiModel("MQ事件数据")
@Data
public abstract class AbstractMqEventMessage<S extends JsonBase, T extends JsonBase> extends AbstractMqMessage<T> {
    @ApiModelProperty("事件前的业务数据")
    private S before;
    @ApiModelProperty("事件后的业务数据")
    private S after;

    @Override
    public String toString() {
        return JacksonUtil.bean2Json(this);
    }
}
