package com.my.mq.commom.demo.log;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

/**
 * 日志对象序列化工具
 *
 * @author xuchaoguo
 */
@Slf4j
public final class LogHelper {
    /**
     * 日志字符串长度限制。超过将自动截取
     */
    private static final int STRING_LOGGING_LIMIT = 256;
    private static ObjectMapper MAPPER = new ObjectMapper();

    private LogHelper() {
    }

    static {
        // 允许出现特殊字符和转义符
        MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        // 如果是空对象的时候,不抛异常
        MAPPER.configure(JsonParser.Feature.IGNORE_UNDEFINED, true);
        MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // 统一序列化规则，将LONG转换为字符串
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.setSerializerModifier(new LogSerializerModifier());
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        MAPPER.registerModule(simpleModule);
    }

    /**
     * 打印对象转字符串
     *
     * @param obj 打印消息
     * @return 打印字符串
     */
    public static String toString(Object obj) {
        if (Objects.isNull(obj)) {
            return null;
        }

        if (obj instanceof String) {
            return truncateLongString((String) obj);
        }

        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("日志处理失败", e);
            return obj.toString();
        }
    }

    /**
     * 自动截取较长的日志字符串。
     *
     * @param logStr 日志字符串
     * @return 截取后的字符串
     */
    public static String truncateLongString(String logStr) {
        if (Objects.isNull(logStr)
                || logStr.length() <= STRING_LOGGING_LIMIT) {
            return logStr;
        }

        return logStr.substring(0, STRING_LOGGING_LIMIT) + "....";
    }
}
