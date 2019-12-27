package com.my.mq.commom.demo.log;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;

/**
 * 超过一定长度的字符串要进行裁剪，避免日志占用太多存储空间。
 * 例如考勤机的人脸、指纹数据等。
 *
 * @author xuchaoguo
 */
public class LogSerializer extends JsonSerializer<Object> {
    private final JsonSerializer defaultSerializer;

    public LogSerializer(JsonSerializer defaultSerializer) {
        this.defaultSerializer = defaultSerializer;
    }

    @Override
    public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        innerSerialize(value, jsonGenerator, serializerProvider);
    }

    @Override
    public void serializeWithType(Object value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider, TypeSerializer typeSer) throws IOException {
        innerSerialize(value, jsonGenerator, serializerProvider);
    }

    private void innerSerialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (value instanceof String) {
            // 仅对字符串类型进行日志截取处理，其他类型保持不变
            String strValue = LogHelper.truncateLongString((String) value);
            jsonGenerator.writeString(strValue);
        } else {
            defaultSerializer.serialize(value, jsonGenerator, serializerProvider);
        }
    }
}
