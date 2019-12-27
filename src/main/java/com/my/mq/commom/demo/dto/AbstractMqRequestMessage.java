package com.my.mq.commom.demo.dto;

import com.my.mq.commom.demo.util.JacksonUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;
@Data
@ApiModel("MQ异步请求消息")
public abstract class AbstractMqRequestMessage<T extends JsonBase> extends AbstractMqMessage{
  @ApiModelProperty("消息接收服务名")
  private String service;

  @Override
  public boolean isValid() {
    return super.isValid() && !StringUtils.isEmpty(service);
  }

  @Override
  public String toString() {
    return JacksonUtil.bean2Json(this);
  }
}
