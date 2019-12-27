package com.my.mq.commom.demo.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.util.StringUtils;

@ApiModel("MQ消息")
@Data
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public abstract class AbstractMqMessage <T extends JsonBase> extends JsonBase{
  @ApiModelProperty("消息类型")
  private String event;
  @ApiModelProperty("消息时间戳（毫秒）")
  private long timestamp;
  @ApiModelProperty("消息版本")
  private int version;
  @ApiModelProperty("消息数据")
  private T eventData;

  public AbstractMqMessage() {
    this.timestamp = System.currentTimeMillis();
    this.version = 0;
  }

  /**
   * 消息是否合法
   *
   * @return 如果合法则返回true，否则false
   */
  public boolean isValid() {
    return !StringUtils.isEmpty(event) && timestamp > 0
        && eventData != null;
  }
}
